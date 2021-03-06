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
package org.apache.rya.api.client.accumulo;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.apache.rya.accumulo.AccumuloRdfConfiguration;
import org.apache.rya.api.client.Install.InstallConfiguration;
import org.apache.rya.api.client.RyaClient;
import org.apache.rya.api.utils.CloseableIterator;
import org.apache.rya.indexing.accumulo.ConfigUtils;
import org.apache.rya.indexing.external.PrecomputedJoinIndexerConfig.PrecomputedJoinStorageType;
import org.apache.rya.indexing.external.PrecomputedJoinIndexerConfig.PrecomputedJoinUpdaterType;
import org.apache.rya.indexing.pcj.storage.PrecomputedJoinStorage;
import org.apache.rya.indexing.pcj.storage.accumulo.AccumuloPcjStorage;
import org.apache.rya.sail.config.RyaSailFactory;
import org.apache.rya.test.accumulo.AccumuloITBase;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.impl.MapBindingSet;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.SailConnection;
import org.junit.Test;

/**
 * Integration tests the methods of {@link AccumuloBatchUpdatePCJ}.
 */
public class AccumuloBatchUpdatePCJIT extends AccumuloITBase {

    private static final String RYA_INSTANCE_NAME = "test_";

    @Test
    public void batchUpdate() throws Exception {
        // Setup a Rya Client.
        final AccumuloConnectionDetails connectionDetails = new AccumuloConnectionDetails(
                super.getUsername(),
                super.getPassword().toCharArray(),
                super.getInstanceName(),
                super.getZookeepers());
        final RyaClient ryaClient = AccumuloRyaClientFactory.build(connectionDetails, super.getConnector());

        // Install an instance of Rya on the mini accumulo cluster.
        ryaClient.getInstall().install(RYA_INSTANCE_NAME, InstallConfiguration.builder()
                .setEnablePcjIndex(true)
                .build());

        Sail sail = null;
        try(final PrecomputedJoinStorage pcjStorage = new AccumuloPcjStorage(super.getConnector(), RYA_INSTANCE_NAME)) {
            // Get a Sail connection backed by the installed Rya instance.
            final AccumuloRdfConfiguration ryaConf = new AccumuloRdfConfiguration();
            ryaConf.setTablePrefix(RYA_INSTANCE_NAME);
            ryaConf.set(ConfigUtils.CLOUDBASE_USER, super.getUsername());
            ryaConf.set(ConfigUtils.CLOUDBASE_PASSWORD, super.getPassword());
            ryaConf.set(ConfigUtils.CLOUDBASE_ZOOKEEPERS, super.getZookeepers());
            ryaConf.set(ConfigUtils.CLOUDBASE_INSTANCE, super.getInstanceName());
            ryaConf.set(ConfigUtils.USE_PCJ, "true");
            ryaConf.set(ConfigUtils.PCJ_STORAGE_TYPE, PrecomputedJoinStorageType.ACCUMULO.toString());
            ryaConf.set(ConfigUtils.PCJ_UPDATER_TYPE, PrecomputedJoinUpdaterType.NO_UPDATE.toString());
            sail = RyaSailFactory.getInstance( ryaConf );

            // Load some statements into the Rya instance.
            final ValueFactory vf = sail.getValueFactory();

            final SailConnection sailConn = sail.getConnection();
            sailConn.begin();
            sailConn.addStatement(vf.createIRI("urn:Alice"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:Bob"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:Charlie"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:David"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:Eve"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:Frank"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:George"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));
            sailConn.addStatement(vf.createIRI("urn:Hillary"), vf.createIRI("urn:likes"), vf.createIRI("urn:icecream"));

            sailConn.addStatement(vf.createIRI("urn:Alice"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:Bob"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:Charlie"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:David"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:Eve"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:Frank"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:blue"));
            sailConn.addStatement(vf.createIRI("urn:George"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:green"));
            sailConn.addStatement(vf.createIRI("urn:Hillary"), vf.createIRI("urn:hasEyeColor"), vf.createIRI("urn:brown"));
            sailConn.commit();
            sailConn.close();

            // Create a PCJ for a SPARQL query.
            final String sparql = "SELECT ?name WHERE { ?name <urn:likes> <urn:icecream> . ?name <urn:hasEyeColor> <urn:blue> . }";
            final String pcjId = pcjStorage.createPcj(sparql);

            // Run the test.
            ryaClient.getBatchUpdatePCJ().batchUpdate(RYA_INSTANCE_NAME, pcjId);

            // Verify the correct results were loaded into the PCJ table.
            final Set<BindingSet> expectedResults = new HashSet<>();

            MapBindingSet bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:Alice"));
            expectedResults.add(bs);

            bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:Bob"));
            expectedResults.add(bs);

            bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:Charlie"));
            expectedResults.add(bs);

            bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:David"));
            expectedResults.add(bs);

            bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:Eve"));
            expectedResults.add(bs);

            bs = new MapBindingSet();
            bs.addBinding("name", vf.createIRI("urn:Frank"));
            expectedResults.add(bs);

            final Set<BindingSet> results = new HashSet<>();
            try(CloseableIterator<BindingSet> resultsIt = pcjStorage.listResults(pcjId)) {
                while(resultsIt.hasNext()) {
                    results.add( resultsIt.next() );
                }
            }

            assertEquals(expectedResults, results);

        } finally {
            if(sail != null) {
                sail.shutDown();
            }
        }
    }
}