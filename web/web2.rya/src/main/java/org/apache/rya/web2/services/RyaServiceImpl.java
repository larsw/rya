package org.apache.rya.web2.services;

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

import org.apache.commons.text.StringEscapeUtils;
import org.apache.rya.api.RdfCloudTripleStoreConfiguration;
import org.apache.rya.api.log.LogUtils;
import org.apache.rya.rdftriplestore.RdfCloudTripleStoreConnection;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.query.impl.IteratingGraphQueryResult;
import org.eclipse.rdf4j.query.impl.IteratingTupleQueryResult;
import org.eclipse.rdf4j.query.parser.*;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static org.apache.rya.api.RdfCloudTripleStoreConstants.VALUE_FACTORY;

@Service
//@Scope(WebApplicationContext.SCOPE_REQUEST)
public class RyaServiceImpl implements RyaService {
    Logger logger = LoggerFactory.getLogger(RyaServiceImpl.class);

    private static final int QUERY_TIME_OUT_SECONDS = 120;

    SailRepository repository;

    public RyaServiceImpl(@Qualifier("sailRepo") SailRepository repository) {
        this.repository = repository;
    }

    @Override
    public QueryResult<?> queryRdf(final String query,
                         final String authorizations,
                         final Optional<String> visibility,
                         final Boolean infer,
                         final Boolean noOutput) {

        SailRepositoryConnection connection = null;
        final Timer timer = setupTimer();

        try {
            connection = repository.getConnection();
            if (StringUtils.isEmpty(query)) {
                throw new InvalidQueryException("The query is empty.");
            }
            final ParsedOperation operation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, query, null);
                if (operation instanceof ParsedGraphQuery) {
                    return performGraphQuery(query, connection, authorizations, infer, noOutput);
                } else if (operation instanceof ParsedTupleQuery) {
                    return performQuery(query, connection, authorizations, infer, noOutput);
                } else if (operation instanceof ParsedUpdate) {
                    // Perform Update Query
                    String updateTime = performUpdate(query, connection, infer, visibility.orElse(null));
                    return new UpdateResult(updateTime);
                } else {
                    throw new MalformedQueryException("Cannot process query. Query type not supported.");
                }

        } catch (final Exception | InvalidQueryException e) {
            logger.error("Error running query", e);
            throw new RuntimeException(e);
        } finally {
            // TODO !!!
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final RepositoryException e) {
//                    logger.error("Error closing connection", e);
//                }
//            }
            timer.cancel();
        }
    }

    private TupleQueryResult performQuery(final String query,
                                          final RepositoryConnection conn,
                                          final String auth,
                                          final Boolean infer,
                                          final Boolean noOutput) throws RepositoryException, MalformedQueryException, QueryEvaluationException, TupleQueryResultHandlerException {
        final TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, query);
        initAuthorizationsAndInference(auth, infer, tupleQuery);
        if (noOutput) {
            //output nothing, but still run query
            tupleQuery.evaluate();
            return new IteratingTupleQueryResult(new ArrayList<>(), new ArrayList<>());
        } else {
//            final CountingTupleQueryResultHandlerWrapper sparqlWriter = new CountingTupleQueryResultHandlerWrapper(handler);
//            final long startTime = System.currentTimeMillis();
            //            logger.info(String.format("Query Time = %.3f   Result Count = %s\n",
//                                   (System.currentTimeMillis() - startTime) / 1000.,
//                                   sparqlWriter.getCount()));

            return tupleQuery.evaluate();
        }
    }

    private GraphQueryResult performGraphQuery(final String query,
                                               final RepositoryConnection connection,
                                               final String authorizations,
                                               final Boolean infer,
                                               final Boolean noOutput)
            throws RepositoryException, MalformedQueryException, QueryEvaluationException, RDFHandlerException {

        final GraphQuery graphQuery = connection.prepareGraphQuery(QueryLanguage.SPARQL, query);
        initAuthorizationsAndInference(authorizations, infer, graphQuery);
        if (noOutput) {
            graphQuery.evaluate();
            return new IteratingGraphQueryResult(new HashMap<>(), new ArrayList<>());
        } else {
            final long startTime = System.currentTimeMillis();
            GraphQueryResult result = graphQuery.evaluate();
            logger.info(String.format("Query Time = %.3f\n", (System.currentTimeMillis() - startTime) / 1000.));
            return result;
        }
    }

    protected String performUpdate(final String query,
                                 final SailRepositoryConnection connection,
                                 final Boolean infer,
                                 final String visibility) throws RepositoryException, MalformedQueryException, InvalidQueryException {
        final Update update = connection.prepareUpdate(QueryLanguage.SPARQL, query);
        if (infer) {
            update.setBinding(RdfCloudTripleStoreConfiguration.CONF_INFER, VALUE_FACTORY.createLiteral(true));
        } else {
            update.removeBinding(RdfCloudTripleStoreConfiguration.CONF_INFER);
        }

        if (connection.getSailConnection() instanceof RdfCloudTripleStoreConnection && visibility != null) {
            final RdfCloudTripleStoreConnection<?> sailConnection = (RdfCloudTripleStoreConnection<?>) connection.getSailConnection();
            sailConnection.getConf().set(RdfCloudTripleStoreConfiguration.CONF_CV, visibility);
        }

        final long startTime = System.currentTimeMillis();

        try {
            update.execute();
        } catch (final UpdateExecutionException e) {
            final String message = "Update could not be successfully completed for query: ";
            logger.error(message + LogUtils.clean(query), e);
            throw new InvalidQueryException(StringEscapeUtils.escapeHtml4(query));
        }

        String updateTime = String.format("Update Time = %.3f\n", (System.currentTimeMillis() - startTime) / 1000.);
        logger.info(updateTime);
        return updateTime;
    }

    private void initAuthorizationsAndInference(String auth, Boolean infer, Query query) {
        if (auth != null && auth.length() > 0) {
            query.setBinding(RdfCloudTripleStoreConfiguration.CONF_QUERY_AUTH, VALUE_FACTORY.createLiteral(auth));
        } else {
            query.removeBinding(RdfCloudTripleStoreConfiguration.CONF_QUERY_AUTH);
        }
        if (infer) {
            query.setBinding(RdfCloudTripleStoreConfiguration.CONF_INFER, VALUE_FACTORY.createLiteral(true));
        } else {
            query.removeBinding(RdfCloudTripleStoreConfiguration.CONF_INFER);
        }
    }

    private Timer setupTimer() {
        final Thread queryThread = Thread.currentThread();
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.debug("interrupting");
                queryThread.interrupt();

            }
        }, QUERY_TIME_OUT_SECONDS * 1000);
        return timer;
    }
}
