package org.apache.rya.web2.services;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryResultHandlerException;
import org.eclipse.rdf4j.query.TupleQueryResultHandler;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;

import java.util.List;

final class CountingTupleQueryResultHandlerWrapper implements TupleQueryResultHandler {
    private final TupleQueryResultHandler innerHandler;
    private int count = 0;

    public CountingTupleQueryResultHandlerWrapper(final TupleQueryResultHandler innerHandler) {
        this.innerHandler = innerHandler;
    }

    public int getCount() {
        return count;
    }

    @Override
    public void endQueryResult() throws TupleQueryResultHandlerException {
        innerHandler.endQueryResult();
    }

    @Override
    public void handleSolution(final BindingSet bindingSet) throws TupleQueryResultHandlerException {
        count++;
        innerHandler.handleSolution(bindingSet);
    }

    @Override
    public void startQueryResult(final List<String> bindingNames) throws TupleQueryResultHandlerException {
        count = 0;
        innerHandler.startQueryResult(bindingNames);
    }

    @Override
    public void handleBoolean(final boolean arg0) throws QueryResultHandlerException {
    }

    @Override
    public void handleLinks(final List<String> arg0) throws QueryResultHandlerException {
    }
}
