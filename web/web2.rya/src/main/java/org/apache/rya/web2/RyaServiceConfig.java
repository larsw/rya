package org.apache.rya.web2;

import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.rya.accumulo.AccumuloRdfConfiguration;
import org.apache.rya.accumulo.AccumuloRyaDAO;
import org.apache.rya.api.persist.RyaDAOException;
import org.apache.rya.prospector.service.ProspectorService;
import org.apache.rya.prospector.service.ProspectorServiceEvalStatsDAO;
import org.apache.rya.rdftriplestore.RdfCloudTripleStore;
import org.apache.rya.rdftriplestore.RyaSailRepository;
import org.apache.rya.rdftriplestore.inference.InferenceEngine;
import org.apache.rya.rdftriplestore.inference.InferenceEngineException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RyaServiceConfig {

    @Value("${rya.instance.name}")
    String zkInstanceName;

    @Value("${rya.instance.zk}")
    String zkInstanceZk;

    @Value("${rya.instance.username}")
    String userName;

    @Value("${rya.instance.password}")
    String password;

    @Value("${rya.tableprefix}")
    String tablePrefix;

    @Value("${rya.displayqueryplan}")
    String displayQueryPlan;

    @Bean
    public ZooKeeperInstance zkInstance() {
        return new ZooKeeperInstance(zkInstanceName, zkInstanceZk);
    }

    @Bean
    public PasswordToken passwordToken() {
        return new PasswordToken(password);
    }

    @Bean
    public Connector connector(ZooKeeperInstance zkInstance, PasswordToken passwordToken)
            throws AccumuloSecurityException, AccumuloException {
        return zkInstance.getConnector(userName, passwordToken);
    }

    @Bean
    public AccumuloRdfConfiguration conf() {
        AccumuloRdfConfiguration c = new AccumuloRdfConfiguration();
        c.setTablePrefix(tablePrefix);
        c.setDisplayQueryPlan(Boolean.parseBoolean(displayQueryPlan));
        return c;
    }

    @Bean(destroyMethod = "destroy")
    public AccumuloRyaDAO ryaDAO(AccumuloRdfConfiguration conf, Connector connector) throws RyaDAOException {
        AccumuloRyaDAO dao = new AccumuloRyaDAO();
        dao.setConnector(connector);
        dao.setConf(conf);
        dao.init();
        return dao;
    }

    @Bean(destroyMethod = "destroy")
    public InferenceEngine inferenceEngine(AccumuloRyaDAO ryaDAO, AccumuloRdfConfiguration conf) throws InferenceEngineException {
        InferenceEngine engine = new InferenceEngine();
        engine.setRyaDAO(ryaDAO);
        engine.setConf(conf);
        engine.init();
        return engine;
    }

    @Bean
    public String prospectTableName(AccumuloRdfConfiguration conf) {
        return ProspectorServiceEvalStatsDAO.getProspectTableName(conf);
    }

    @Bean
    public ProspectorService prospectorService(Connector connector, String prospectorTableName) throws AccumuloSecurityException, AccumuloException {
        return new ProspectorService(connector, prospectorTableName);
    }

    @Bean
    public ProspectorServiceEvalStatsDAO rdfEvalStatsDAO(ProspectorService prospectorService, AccumuloRdfConfiguration conf) {
        return new ProspectorServiceEvalStatsDAO(prospectorService, conf);
    }

    @Bean
    public RdfCloudTripleStore rts(AccumuloRyaDAO ryaDAO, ProspectorServiceEvalStatsDAO rdfEvalStatsDAO, InferenceEngine inferenceEngine, AccumuloRdfConfiguration conf) {
        RdfCloudTripleStore store = new RdfCloudTripleStore();
        store.setRyaDAO(ryaDAO);
        store.setRdfEvalStatsDAO(rdfEvalStatsDAO);
        store.setInferenceEngine(inferenceEngine);
        store.setConf(conf);
        return store;
    }

    @Bean
    public RyaSailRepository sailRepo(RdfCloudTripleStore rts) {
        RyaSailRepository repository = new RyaSailRepository(rts);
        repository.init();
        return repository;
    }
}
