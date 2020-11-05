package org.apache.rya.web2.converters;

import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SparqlStarXmlMessageConverterTests extends MessageConverterBaseFixture<SparqlStarXmlMessageConverter> {
    protected SparqlStarXmlMessageConverterTests() {
        super(new SparqlStarXmlMessageConverter(), TupleQueryResultFormat.SPARQL_STAR.getDefaultMIMEType());
    }

    @Test
    public void testCanWriteSparqlStarResponse() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        Document xDoc = getXmlDocumentFromOutputMessage();
        XPath xpath = XPathFactory.newInstance().newXPath();
        Boolean tripleExists = (Boolean)xpath.compile("boolean(/sparql/results/result/binding/triple)").evaluate(xDoc, XPathConstants.BOOLEAN);
        assertTrue(tripleExists);
    }
}
