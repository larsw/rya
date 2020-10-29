package org.apache.cloud.rdf.web.sail;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import static org.apache.rya.api.RdfCloudTripleStoreConstants.VALUE_FACTORY;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.rya.api.RdfCloudTripleStoreConfiguration;
import org.apache.rya.api.log.LogUtils;
import org.apache.rya.api.security.SecurityProvider;
import org.apache.rya.rdftriplestore.RdfCloudTripleStoreConnection;
import org.apache.rya.rdftriplestore.utils.RdfFormatUtils;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.query.parser.ParsedGraphQuery;
import org.eclipse.rdf4j.query.parser.ParsedOperation;
import org.eclipse.rdf4j.query.parser.ParsedTupleQuery;
import org.eclipse.rdf4j.query.parser.ParsedUpdate;
import org.eclipse.rdf4j.query.parser.QueryParserUtil;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLStarResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqlstarjson.SPARQLStarResultsJSONWriterFactory;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLStarResultsXMLWriter;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.rdfxml.RDFXMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Class RdfController
 * Date: Mar 7, 2012
 * Time: 11:07:19 AM
 */
@Controller
public class RdfController {
    private static final Logger log = Logger.getLogger(RdfController.class);

    private static final int QUERY_TIME_OUT_SECONDS = 120;

    @Qualifier("sailRepo")
    @Autowired
    SailRepository repository;

    @Autowired
    SecurityProvider provider;

    @RequestMapping(value = "/queryrdf", method = {RequestMethod.GET, RequestMethod.POST})
    public void queryRdf(@RequestParam("query") final String query,
                         @RequestParam(value = RdfCloudTripleStoreConfiguration.CONF_QUERY_AUTH, required = false) String auth,
                         @RequestParam(value = RdfCloudTripleStoreConfiguration.CONF_CV, required = false) final String vis,
                         @RequestParam(value = RdfCloudTripleStoreConfiguration.CONF_INFER, required = false) final String infer,
                         @RequestParam(value = "nullout", required = false) final String nullout,
                         @RequestParam(value = RdfCloudTripleStoreConfiguration.CONF_RESULT_FORMAT, required = false) final String emit,
                         @RequestParam(value = "padding", required = false) final String padding,
                         @RequestParam(value = "callback", required = false) final String callback,
                         @RequestParam(value = "reify", required = false) final Boolean reify,
                         final HttpServletRequest request,
                         final HttpServletResponse response) {
        // WARNING: if you add to the above request variables,
        // Be sure to validate and encode since they come from the outside and could contain odd damaging character sequences.
        SailRepositoryConnection conn = null;
        final Thread queryThread = Thread.currentThread();
        auth = StringUtils.arrayToCommaDelimitedString(provider.getUserAuths(request));
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                log.debug("interrupting");
                queryThread.interrupt();

            }
        }, QUERY_TIME_OUT_SECONDS * 1000);

        try {
            final ServletOutputStream os = response.getOutputStream();
            conn = repository.getConnection();

            final Boolean isBlankQuery = StringUtils.isEmpty(query);
            final ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null);

            final Boolean requestedCallback = !StringUtils.isEmpty(callback);
            final Boolean requestedFormat = !StringUtils.isEmpty(emit);

            if (!isBlankQuery) {
                if (operation instanceof ParsedGraphQuery) {
                    // Perform Graph Query
                    final RDFHandler handler = new RDFXMLWriter(os);
                    response.setContentType("text/xml");
                    performGraphQuery(query, conn, auth, infer, nullout, handler);
                } else if (operation instanceof ParsedTupleQuery) {
                    // Perform Tuple Query
                    TupleQueryResultHandler handler;

                    String acceptHeaderValue = request.getHeader("Accept");

                    if (acceptHeaderValue != null) {
                        switch (acceptHeaderValue.toLowerCase(Locale.getDefault())) {
                            case "application/x-sparqlstar-results+json":
                                handler = new SPARQLStarResultsJSONWriter(os);
                                response.setContentType("application/x-sparqlstar-results+json");
                                break;
                            case "application/x-sparql-results+json":
                                handler = new SPARQLResultsJSONWriter(os);
                                response.setContentType("application/x-sparqlstar-results+json");
                                break;
                            case "application/x-sparqlstar-results+xml":
                                handler = new SPARQLStarResultsXMLWriter(os);
                                response.setContentType("application/x-sparqlstar-results+xml");
                                break;
                            case "application/x-sparql-results+xml":
                                handler = new SPARQLResultsXMLWriter(os);
                                response.setContentType("application/x-sparql-results+xml");
                                break;
                            default:
                                handler = new SPARQLResultsXMLWriter(os);
                                response.setContentType("text/xml");
                        }
                    } else {
                        handler = new SPARQLResultsXMLWriter(os);
                        response.setContentType("text/xml");
                    }

                    performQuery(query, conn, auth, infer, nullout, handler);
                } else if (operation instanceof ParsedUpdate) {
                    // Perform Update Query
                    performUpdate(query, conn, os, infer, vis);
                } else {
                    throw new MalformedQueryException("Cannot process query. Query type not supported.");
                }
            }

            if (requestedCallback) {
                os.print(")");
            }
        } catch (final Exception e) {
            log.error("Error running query", e);
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (final RepositoryException e) {
                    log.error("Error closing connection", e);
                }
            }
        }

        timer.cancel();
    }

    private void performQuery(final String query, final RepositoryConnection conn, final String auth, final String infer, final String nullout, final TupleQueryResultHandler handler) throws RepositoryException, MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException {
        final TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
        initAuthorizationsAndInference(auth, infer, tupleQuery);
        if (nullout != null && nullout.length() > 0) {
            //output nothing, but still run query
            tupleQuery.evaluate(new TupleQueryResultHandler() {
                @Override
                public void startQueryResult(final List<String> strings) throws TupleQueryResultHandlerException {
                }

                @Override
                public void endQueryResult() throws TupleQueryResultHandlerException {
                }

                @Override
                public void handleSolution(final BindingSet bindings) throws TupleQueryResultHandlerException {
                }

                @Override
                public void handleBoolean(final boolean arg0) throws QueryResultHandlerException {
                }

                @Override
                public void handleLinks(final List<String> arg0) throws QueryResultHandlerException {
                }
            });
        } else {
            final CountingTupleQueryResultHandlerWrapper sparqlWriter = new CountingTupleQueryResultHandlerWrapper(handler);
            final long startTime = System.currentTimeMillis();
            tupleQuery.evaluate(sparqlWriter);
            log.info(String.format("Query Time = %.3f   Result Count = %s\n",
                                   (System.currentTimeMillis() - startTime) / 1000.,
                                   sparqlWriter.getCount()));
        }

    }

    private void performGraphQuery(final String query, final RepositoryConnection conn, final String auth, final String infer, final String nullout, final RDFHandler handler) throws RepositoryException, MalformedQueryException, QueryEvaluationException, RDFHandlerException {
        final GraphQuery graphQuery = conn.prepareGraphQuery(QueryLanguage.SPARQL, query);
        initAuthorizationsAndInference(auth, infer, graphQuery);
        if (nullout != null && nullout.length() > 0) {
            //output nothing, but still run query
            // TODO this seems like a strange use case.
            graphQuery.evaluate(new RDFHandler() {
                @Override
                public void startRDF() throws RDFHandlerException {
                }

                @Override
                public void endRDF() throws RDFHandlerException {
                }

                @Override
                public void handleNamespace(final String prefix, final String uri)
                        throws RDFHandlerException {
                }

                @Override
                public void handleStatement(final Statement st)
                        throws RDFHandlerException {
                }

                @Override
                public void handleComment(final String comment)
                        throws RDFHandlerException {
                }
            });
        } else {
            final long startTime = System.currentTimeMillis();
            graphQuery.evaluate(handler);
            log.info(String.format("Query Time = %.3f\n", (System.currentTimeMillis() - startTime) / 1000.));
        }

    }

    private void initAuthorizationsAndInference(String auth, String infer, Query graphQuery) {
        if (auth != null && auth.length() > 0) {
            graphQuery.setBinding(RdfCloudTripleStoreConfiguration.CONF_QUERY_AUTH, VALUE_FACTORY.createLiteral(auth));
        }
        if (infer != null && infer.length() > 0) {
            graphQuery.setBinding(RdfCloudTripleStoreConfiguration.CONF_INFER, VALUE_FACTORY.createLiteral(Boolean.parseBoolean(infer)));
        }
    }

    protected void performUpdate(final String query, final SailRepositoryConnection conn, final ServletOutputStream os, final String infer, final String vis) throws RepositoryException, MalformedQueryException, IOException {
        final Update update = conn.prepareUpdate(QueryLanguage.SPARQL, query);
        if (infer != null && infer.length() > 0) {
            update.setBinding(RdfCloudTripleStoreConfiguration.CONF_INFER, VALUE_FACTORY.createLiteral(Boolean.parseBoolean(infer)));
        }

        if (conn.getSailConnection() instanceof RdfCloudTripleStoreConnection && vis != null) {
            final RdfCloudTripleStoreConnection<?> sailConnection = (RdfCloudTripleStoreConnection<?>) conn.getSailConnection();
            sailConnection.getConf().set(RdfCloudTripleStoreConfiguration.CONF_CV, vis);
        }

        final long startTime = System.currentTimeMillis();

        try {
            update.execute();
        } catch (final UpdateExecutionException e) {
            final String message = "Update could not be successfully completed for query: ";
            os.print(String.format(message + "%s\n\n", StringEscapeUtils.escapeHtml4(query)));
            log.error(message + LogUtils.clean(query), e);
        }

        log.info(String.format("Update Time = %.3f\n", (System.currentTimeMillis() - startTime) / 1000.));
    }

    private static final class CountingTupleQueryResultHandlerWrapper implements TupleQueryResultHandler {
        private final TupleQueryResultHandler indir;
        private int count = 0;

        public CountingTupleQueryResultHandlerWrapper(final TupleQueryResultHandler indir){
            this.indir = indir;
        }

        public int getCount() {
            return count;
        }

        @Override
        public void endQueryResult() throws TupleQueryResultHandlerException {
            indir.endQueryResult();
        }

        @Override
        public void handleSolution(final BindingSet bindingSet) throws TupleQueryResultHandlerException {
            count++;
            indir.handleSolution(bindingSet);
        }

        @Override
        public void startQueryResult(final List<String> bindingNames) throws TupleQueryResultHandlerException {
            count = 0;
            indir.startQueryResult(bindingNames);
        }

        @Override
        public void handleBoolean(final boolean arg0) throws QueryResultHandlerException {
        }

        @Override
        public void handleLinks(final List<String> arg0) throws QueryResultHandlerException {
        }
    }

    @RequestMapping(value = "/loadrdf", method = RequestMethod.POST)
    public void loadRdf(
            @RequestParam(required = false) final String format,
            @RequestParam(value = RdfCloudTripleStoreConfiguration.CONF_CV, required = false) final String cv,
            @RequestParam(required = false) final String graph,
            @RequestBody final String body,
            final HttpServletResponse response)
            throws RepositoryException, IOException, RDFParseException {
        RDFFormat format_r = RDFFormat.RDFXML;
        if (format != null) {
            format_r = RdfFormatUtils.getRdfFormatFromName(format);
            if (format_r == null) {
                throw new RuntimeException("RDFFormat[" + format + "] not found");
            }
        }

        // add named graph as context (if specified).
        final List<Resource> contextList = new ArrayList<Resource>();
        if (graph != null) {
            contextList.add(VALUE_FACTORY.createIRI(graph));
        }
        SailRepositoryConnection conn = null;
        try {
            conn = repository.getConnection();

            if (conn.getSailConnection() instanceof RdfCloudTripleStoreConnection && cv != null) {
                final RdfCloudTripleStoreConnection<?> sailConnection = (RdfCloudTripleStoreConnection<?>) conn.getSailConnection();
                sailConnection.getConf().set(RdfCloudTripleStoreConfiguration.CONF_CV, cv);
            }

            conn.add(new StringReader(body), "", format_r, contextList.toArray(new Resource[contextList.size()]));
            conn.commit();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
