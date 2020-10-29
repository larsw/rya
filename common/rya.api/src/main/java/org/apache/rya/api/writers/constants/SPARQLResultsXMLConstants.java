package org.apache.rya.api.writers.constants;

public interface SPARQLResultsXMLConstants {

    public static final String NAMESPACE = "http://www.w3.org/2005/sparql-results#";

    public static final String ROOT_TAG = "sparql";

    public static final String HEAD_TAG = "head";

    public static final String LINK_TAG = "link";

    public static final String VAR_TAG = "variable";

    public static final String VAR_NAME_ATT = "name";

    public static final String HREF_ATT = "href";

    public static final String BOOLEAN_TAG = "boolean";

    public static final String BOOLEAN_TRUE = "true";

    public static final String BOOLEAN_FALSE = "false";

    public static final String RESULT_SET_TAG = "results";

    public static final String RESULT_TAG = "result";

    public static final String BINDING_TAG = "binding";

    public static final String BINDING_NAME_ATT = "name";

    public static final String URI_TAG = "uri";

    public static final String BNODE_TAG = "bnode";

    public static final String LITERAL_TAG = "literal";

    public static final String LITERAL_LANG_ATT = "xml:lang";

    public static final String LITERAL_DATATYPE_ATT = "datatype";

    public static final String UNBOUND_TAG = "unbound";

    public static final String QNAME = "q:qname";

    /* tag constants for serialization of RDF* values in results */

    public static final String TRIPLE_TAG = "triple";

    /* Stardog variant */
    public static final String STATEMENT_TAG = "statement";

    public static final String SUBJECT_TAG = "subject";
    /* Stardog variant */
    public static final String S_TAG = "s";

    public static final String PREDICATE_TAG = "predicate";
    /* Stardog variant */
    public static final String P_TAG = "p";

    public static final String OBJECT_TAG = "object";
    /* Stardog variant */
    public static final String O_TAG = "o";
}