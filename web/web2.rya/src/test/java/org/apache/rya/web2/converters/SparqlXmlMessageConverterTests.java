package org.apache.rya.web2.converters;

import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.rio.helpers.RDFStarUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


public class SparqlXmlMessageConverterTests extends MessageConverterBaseFixture<SparqlXmlMessageConverter> {

    protected SparqlXmlMessageConverterTests() {
        super(new SparqlXmlMessageConverter(), TupleQueryResultFormat.SPARQL.getDefaultMIMEType());
    }

    @Test
    public void testCanWriteSparqlResponse() throws IOException, ParserConfigurationException, SAXException, XPathExpressionException {
        Document xDoc = getXmlDocumentFromOutputMessage();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String uriValue = (String) xpath.compile("/sparql/results/result/binding/uri/text()").evaluate(xDoc, XPathConstants.STRING);

        assertNotNull(uriValue);
        assertTrue(uriValue.startsWith(RDFStarUtil.TRIPLE_PREFIX));
    }
}
