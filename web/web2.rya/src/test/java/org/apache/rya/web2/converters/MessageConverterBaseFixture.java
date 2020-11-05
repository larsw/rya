package org.apache.rya.web2.converters;

import org.eclipse.rdf4j.model.Triple;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.util.Statements;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.impl.ListBindingSet;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageConverterBaseFixture<TConverter extends HttpMessageConverter<TupleQueryResult>> {
    protected final TConverter converter;
    protected final MediaType mediaType;
    protected MockTupleQueryResult tupleQueryResult = null;
    private MockHttpOutputMessage outputMessage;

    protected MessageConverterBaseFixture(TConverter converter, String mimeType) {
        this.converter = converter;
        this.mediaType = MediaType.valueOf(mimeType);
    }

    @BeforeEach
    public void setup() throws IOException, SAXException, ParserConfigurationException {

        ValueFactory vf = SimpleValueFactory.getInstance();
        String ex = "http://example.org/#";
        ArrayList<BindingSet> bindingSets = new ArrayList<>();
        List<String> names = Collections.singletonList("s");

        Triple triple = Statements.toTriple(
                vf.createStatement(
                        vf.createIRI(ex, "foo"),
                        vf.createIRI(ex, "KNOWS"),
                        vf.createIRI(ex, "bar")
                ));

        List<Value> values = Collections.singletonList(triple);
        BindingSet bs1 = new ListBindingSet(names, values);
        bindingSets.add(bs1);
        this.tupleQueryResult = new MockTupleQueryResult(new ArrayList<>(), bindingSets);

        this.outputMessage = new MockHttpOutputMessage();
        this.converter.write(this.tupleQueryResult, this.mediaType, outputMessage);
    }

    protected String getStringFromOutputMessage() {
        return outputMessage.getBodyAsString();
    }

    protected Document getXmlDocumentFromOutputMessage() throws ParserConfigurationException, SAXException, IOException {
        final InputStream inputStream = new ByteArrayInputStream(outputMessage.getBodyAsBytes());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = docBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(inputStream);
    }
}
