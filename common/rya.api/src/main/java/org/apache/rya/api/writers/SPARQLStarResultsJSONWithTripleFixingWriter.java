package org.apache.rya.api.writers;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Triple;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLStarResultsJSONWriter;
import org.eclipse.rdf4j.rio.helpers.RDFStarUtil;

public class SPARQLStarResultsJSONWithTripleFixingWriter extends SPARQLStarResultsJSONWriter {
    
    public SPARQLStarResultsJSONWithTripleFixingWriter(OutputStream out) {
        super(out);
    }
    
    @Override
	protected void writeValue(Value value) throws IOException, QueryResultHandlerException {
        jg.writeStartObject();
        
        if (value instanceof Triple) {
            writeTriple((Triple) value);
        } else if (value instanceof IRI) {
            IRI iri = (IRI) value;
            if (RDFStarUtil.isEncodedTriple(iri)) {
                writeValue(RDFStarUtil.fromRDFEncodedValue(iri));
            } else {
                jg.writeStringField("type", "uri");
                jg.writeStringField("value", iri.toString());
            }

		} else if (value instanceof BNode) {
			jg.writeStringField("type", "bnode");
			jg.writeStringField("value", ((BNode) value).getID());
		} else if (value instanceof Literal) {
			Literal lit = (Literal) value;

			if (Literals.isLanguageLiteral(lit)) {
				jg.writeObjectField("xml:lang", lit.getLanguage().orElse(null));
			} else {
				IRI datatype = lit.getDatatype();
				boolean ignoreDatatype = datatype.equals(XSD.STRING) && xsdStringToPlainLiteral();
				if (!ignoreDatatype) {
					jg.writeObjectField("datatype", lit.getDatatype().stringValue());
				}
			}

			jg.writeObjectField("type", "literal");

			jg.writeObjectField("value", lit.getLabel());
		} else {
			throw new TupleQueryResultHandlerException("Unknown Value object type: " + value.getClass());
		}
		jg.writeEndObject();
    }
    
    private void writeTriple(Triple triple) throws IOException {
        jg.writeObjectFieldStart("triple");  // "triple": {
        jg.writeFieldName("subject");        //   "subject": 
        writeValue(triple.getSubject());     //      <subject>
        jg.writeEndObject();                 //    ,
        jg.writeFieldName("predicate");      //   "predicate": 
        writeValue(triple.getPredicate());   //     <predicate>
        jg.writeEndObject();                 //    ,
        jg.writeFieldName("object");         //    "object":
        writeValue(triple.getObject());      //      <object>
        //jg.writeEndObject();                 //
        jg.writeEndObject();                 // }
    }
}