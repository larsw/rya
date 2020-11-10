package org.apache.rya.web2.services;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryResult;

import java.io.Closeable;
import java.io.IOException;

//public class ClosableResultWrapper<T> implements Closeable, QueryResult<T> {
//
//    private final QueryResult<T> result;
//    private final Closeable closeable;
//
//    public ClosableResultWrapper(QueryResult<T> result, Closeable closeable) {
//
//        this.result = result;
//        this.closeable = closeable;
//    }
//
//    @Override
//    public void close() throws IOException {
//        closeable.close();
//    }
//
//    @Override
//    public boolean hasNext() throws QueryEvaluationException {
//        return result.hasNext();
//    }
//
//    @Override
//    public T next() throws QueryEvaluationException {
//        return result.next();
//    }
//
//    @Override
//    public void remove() throws QueryEvaluationException {
//        result.remove();
//    }
//}
