package org.apache.rya.api.writers;

import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.util.Literals;
import org.eclipse.rdf4j.model.vocabulary.XSD;
import org.eclipse.rdf4j.query.Binding;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.resultio.BasicQueryWriterSettings;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLStarResultsXMLWriter;
import org.eclipse.rdf4j.rio.helpers.RDFStarUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Map;

import static org.apache.rya.api.writers.constants.SPARQLResultsXMLConstants.*;

public class SPARQLStarResultsXMLWithTripleFixingWriter extends SPARQLStarResultsXMLWriter {
    public SPARQLStarResultsXMLWithTripleFixingWriter(OutputStream out) {
        super(out);
    }

    static Field namespaceTableField;

    static {
        try {
            namespaceTableField = SPARQLResultsXMLWriter.class
                    .getSuperclass()
                    .getDeclaredField("namespaceTable");
            namespaceTableField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    Map<String, String> namespaceTable;

    /**
     * HACK
     * @return non-public namespaceTable field of super class.
     */
    protected Map<String, String> getNamespaceTable(){
        if (namespaceTable != null) {
            return namespaceTable;
        }
        Object obj = null;
        try {
            obj = namespaceTableField.get(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //noinspection unchecked
        namespaceTable = (Map<String, String>)obj;
        return namespaceTable;
    }

    @Override
    protected void handleSolutionImpl(BindingSet bindingSet) throws TupleQueryResultHandlerException {
        try {
            if (!documentOpen) {
                startDocument();
            }

            if (!headerOpen) {
                startHeader();
            }

            if (!headerComplete) {
                endHeader();
            }

            if (!tupleVariablesFound) {
                throw new IllegalStateException("Must call startQueryResult before handleSolution");
            }

            xmlWriter.startTag(RESULT_TAG);

            for (Binding binding : bindingSet) {
                xmlWriter.setAttribute(BINDING_NAME_ATT, binding.getName());
                xmlWriter.startTag(BINDING_TAG);

                writeValue(binding.getValue());

                xmlWriter.endTag(BINDING_TAG);
            }

            xmlWriter.endTag(RESULT_TAG);
        } catch (IOException | QueryResultHandlerException e) {
            throw new TupleQueryResultHandlerException(e);
        }
    }
    private boolean isQName(IRI nextUri) {
        return getNamespaceTable().containsKey(nextUri.getNamespace());
    }

    private void writeQName(IRI nextUri) {
        if (getWriterConfig().get(BasicQueryWriterSettings.ADD_SESAME_QNAME)) {
            xmlWriter.setAttribute(QNAME, getNamespaceTable().get(nextUri.getNamespace()) + ":" + nextUri.getLocalName());
        }
    }

    private void writeURI(IRI uri) throws IOException {
        if (isQName(uri)) {
            writeQName(uri);
        }
        xmlWriter.textElement(URI_TAG, uri.toString());
    }

    private void writeBNode(BNode bNode) throws IOException {
        xmlWriter.textElement(BNODE_TAG, bNode.getID());
    }

    private void writeLiteral(Literal literal) throws IOException {
        if (Literals.isLanguageLiteral(literal)) {
            //noinspection OptionalGetWithoutIsPresent
            xmlWriter.setAttribute(LITERAL_LANG_ATT, literal.getLanguage().get());
        }
        // Only enter this section for non-language literals now, as the
        // rdf:langString datatype is handled implicitly above
        else {
            IRI datatype = literal.getDatatype();
            boolean ignoreDatatype = datatype.equals(XSD.STRING) && xsdStringToPlainLiteral();
            if (!ignoreDatatype) {
                if (isQName(datatype)) {
                    writeQName(datatype);
                }
                xmlWriter.setAttribute(LITERAL_DATATYPE_ATT, datatype.stringValue());
            }
        }

        xmlWriter.textElement(LITERAL_TAG, literal.getLabel());
    }

    private void writeValue(Value value) throws IOException {
        if (value instanceof Triple) {
            writeTriple((Triple) value);
        } else if (value instanceof IRI) {
            IRI iri = (IRI) value;
            if (RDFStarUtil.isEncodedTriple(iri)) {
                Value val = RDFStarUtil.fromRDFEncodedValue(iri);
                if (val instanceof Triple) {
                    writeTriple((Triple)val);
                } else {
                    writeValue(val);
                }
            } else {
                writeURI((IRI) value);
            }
        } else if (value instanceof BNode) {
            writeBNode((BNode) value);
        } else if (value instanceof Literal) {
            writeLiteral((Literal) value);
        }
    }

    private void writeTriple(Triple triple) throws IOException {
        xmlWriter.startTag(TRIPLE_TAG);
        xmlWriter.startTag(SUBJECT_TAG);
        writeValue(triple.getSubject());
        xmlWriter.endTag(SUBJECT_TAG);
        xmlWriter.startTag(PREDICATE_TAG);
        writeValue(triple.getPredicate());
        xmlWriter.endTag(PREDICATE_TAG);
        xmlWriter.startTag(OBJECT_TAG);
        writeValue(triple.getObject());
        xmlWriter.endTag(OBJECT_TAG);
        xmlWriter.endTag(TRIPLE_TAG);
    }
}
