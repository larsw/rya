package org.apache.rya.web2.services;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResult;

public class UpdateDummyResult implements QueryResult<Object> {
    @Override
    public void close() throws QueryEvaluationException {

    }

    @Override
    public boolean hasNext() throws QueryEvaluationException {
        return false;
    }

    @Override
    public Object next() throws QueryEvaluationException {
        return null;
    }

    @Override
    public void remove() throws QueryEvaluationException {

    }
}
