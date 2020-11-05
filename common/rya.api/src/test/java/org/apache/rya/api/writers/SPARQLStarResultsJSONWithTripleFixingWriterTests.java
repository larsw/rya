package org.apache.rya.api.writers;

import org.eclipse.rdf4j.model.Triple;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.impl.ListBindingSet;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLStarResultsJSONWriter;
import org.eclipse.rdf4j.rio.helpers.RDFStarUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SPARQLStarResultsJSONWithTripleFixingWriterTests {
    
    @Test
    public void testTripleValuePersistence() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ValueFactory vf = SimpleValueFactory.getInstance();
        SPARQLStarResultsJSONWriter writer = new SPARQLStarResultsJSONWithTripleFixingWriter(os);
        Triple triple = vf.createTriple(
          vf.createIRI(":s"), vf.createIRI(":p"), vf.createLiteral("baz")
        );
        Triple val = vf.createTriple(RDFStarUtil.toRDFEncodedValue(triple), vf.createIRI(":x"), vf.createLiteral(1));
        List<String> names = new ArrayList<>();
        names.add("a");
        BindingSet bindingSet = new ListBindingSet(names, val);
        List<String> columns = new ArrayList<>();
        columns.add("a");
        writer.startDocument();
        writer.startQueryResult(columns);
        writer.handleSolution(bindingSet);
        writer.endQueryResult();
        os.flush();
        String result = new String(os.toByteArray());
        System.out.println(result);
    }
}