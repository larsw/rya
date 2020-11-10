package org.apache.rya.web2.converters;

import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MockTupleQueryResult implements TupleQueryResult {

    private final Iterator<BindingSet> iterator;
    private List<String> bindingNames;
    private Collection<BindingSet> bindingSets;

    MockTupleQueryResult(final List<String> bindingNames,
                         final Collection<BindingSet> bindingSets) {

        this.bindingNames = bindingNames;
        this.bindingSets = bindingSets;
        this.iterator = bindingSets.iterator();
    }

    @Override
    public List<String> getBindingNames() throws QueryEvaluationException {
        return new ArrayList<>();
    }

    @Override
    public void close() throws QueryEvaluationException {
    }

    @Override
    public boolean hasNext() throws QueryEvaluationException {
        return iterator.hasNext();
    }

    @Override
    public BindingSet next() throws QueryEvaluationException {
        return iterator.next();
    }

    @Override
    public void remove() throws QueryEvaluationException {
        iterator.remove();
    }
}
