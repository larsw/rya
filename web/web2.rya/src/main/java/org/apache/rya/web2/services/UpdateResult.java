package org.apache.rya.web2.services;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResult;

public class UpdateResult implements QueryResult<Object> {

    private final String updateTime;

    public String getUpdateTime() {
        return updateTime;
    }

    public UpdateResult(String updateTime) {

        this.updateTime = updateTime;
    }

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
