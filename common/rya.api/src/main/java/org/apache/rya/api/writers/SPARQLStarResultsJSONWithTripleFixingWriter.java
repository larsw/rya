package org.apache.rya.api.writers;

import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLStarResultsJSONWriter;
import org.eclipse.rdf4j.rio.helpers.RDFStarUtil;

import java.io.IOException;
import java.io.OutputStream;

public class SPARQLStarResultsJSONWithTripleFixingWriter extends SPARQLStarResultsJSONWriter {
    
    public SPARQLStarResultsJSONWithTripleFixingWriter(OutputStream out) {
        super(out);
    }
    
    @Override
	protected void writeValue(Value value) throws IOException, QueryResultHandlerException {
        if (RDFStarUtil.isEncodedTriple(value)) {
            writeValue(RDFStarUtil.fromRDFEncodedValue(value));
        } else {
            super.writeValue(value);
        }
    }
}