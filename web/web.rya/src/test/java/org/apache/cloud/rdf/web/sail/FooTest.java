package org.apache.cloud.rdf.web.sail;


import junit.framework.TestCase;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.AuthenticationToken;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.rya.accumulo.AccumuloRdfConfiguration;
import org.apache.rya.accumulo.AccumuloRyaDAO;
import org.apache.rya.rdftriplestore.RdfCloudTripleStore;
import org.apache.rya.rdftriplestore.RdfCloudTripleStoreConnection;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.*;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.List;

public class FooTest extends TestCase {

    private SailRepository repository;
    private SailRepositoryConnection connection;
    private static final ValueFactory VF = SimpleValueFactory.getInstance();
    private Connector connector;
    private RdfCloudTripleStoreConnection<AccumuloRdfConfiguration> storeConnection;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.DEBUG);

//Define log pattern layout
        PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");

//Add console appender to root logger
        rootLogger.addAppender(new ConsoleAppender(layout));

        PasswordToken passwordToken = new PasswordToken();
        AuthenticationToken.Properties props = new AuthenticationToken.Properties();
        props.put("password", "");
        passwordToken.init(props);
        connector = new MockInstance().getConnector("", passwordToken);

        RdfCloudTripleStore sail = new RdfCloudTripleStore<AccumuloRdfConfiguration>();
        AccumuloRdfConfiguration conf = new AccumuloRdfConfiguration();
        conf.setTablePrefix("lubm_");
        sail.setConf(conf);
        AccumuloRyaDAO crdfdao = new AccumuloRyaDAO();
        crdfdao.setConnector(connector);
        crdfdao.setConf(conf);
        sail.setRyaDAO(crdfdao);

        repository = new SailRepository(sail);
        connection = repository.getConnection();

        storeConnection = new RdfCloudTripleStoreConnection<AccumuloRdfConfiguration>(sail, conf, SimpleValueFactory.getInstance());

//        loadRDFStarData();
    }

//    private void loadRDFStarData() throws RepositoryException, DatatypeConfigurationException {
//        final String ns = "http://foo.com/#";
//
//        storeConnection.begin();
//
//
//        storeConnection.addStatement(RDFStarUtil.toRDFEncodedValue(VF.createTriple(
//                VF.createIRI(ns, "john"),
//                VF.createIRI(ns, "knows"),
//                VF.createIRI(ns, "dave"))),
//                VF.createIRI(ns, "credibility"),
//                VF.createLiteral(0.5));
//
//        Statement stmt = VF.createStatement(VF.createIRI(ns, "john"),
//                VF.createIRI(ns, "knows"),
//                VF.createIRI(ns, "dave"));
//        storeConnection.addStatement((Resource)stmt, VF.createIRI(ns, "credibility"), VF.createLiteral(0.5));
//        storeConnection.commit();
//    }

    @Test
    public void testTheTest() throws Exception {
        RdfController controller = new RdfController();
        controller.repository = repository;
        final String query = "PREFIX : <http://foobar.com/#>\nINSERT DATA{\n <<:john :knows :jack>> :credibility \"0.5\" .}";

        ServletOutputStream os = new ServletOutputStream() {
            int i;

            @Override
            public void write(int i) throws IOException {
                this.i = i;
            }
        };
        controller.performUpdate(query, connection, os, null, null);

        String q = "SELECT ?s ?p ?o WHERE {?s ?p ?o}";

        TupleQuery tupleQuery = connection.prepareTupleQuery(QueryLanguage.SPARQL, q);

        tupleQuery.evaluate(new PrintTupleHandler());
    }

    private static class PrintTupleHandler implements TupleQueryResultHandler {

        @Override
        public void startQueryResult(List<String> strings) throws TupleQueryResultHandlerException {
        }

        @Override
        public void endQueryResult() throws TupleQueryResultHandlerException {

        }

        @Override
        public void handleSolution(BindingSet bindingSet) throws TupleQueryResultHandlerException {
            System.out.println(bindingSet);
        }

        @Override
        public void handleBoolean(boolean paramBoolean) throws QueryResultHandlerException {
        }

        @Override
        public void handleLinks(List<String> paramList) throws QueryResultHandlerException {
        }
    }

}
