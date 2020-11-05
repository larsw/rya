package org.apache.rya.web2.converters;

import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.tukaani.xz.UnsupportedOptionsException;

import java.io.IOException;
import java.io.OutputStream;

public class SparqlXmlMessageConverter extends AbstractHttpMessageConverter<TupleQueryResult> {

    public SparqlXmlMessageConverter() {
        super(MediaType.valueOf(TupleQueryResultFormat.SPARQL.getDefaultMIMEType()));
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return TupleQueryResult.class.isAssignableFrom(clazz);
    }

    @Override
    protected boolean canRead(MediaType mediaType) {
        return false;
    }

    @Override
    protected TupleQueryResult readInternal(Class<? extends TupleQueryResult> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        throw new UnsupportedOptionsException();
    }

    @Override
    protected void writeInternal(TupleQueryResult result, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        OutputStream os = outputMessage.getBody();
        SPARQLResultsXMLWriter writer = new SPARQLResultsXMLWriter(os);
        QueryResults.report(result, writer);
    }
}
