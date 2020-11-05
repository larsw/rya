package org.apache.rya.web2.services;

import org.eclipse.rdf4j.query.QueryResult;

import java.util.Optional;
import java.util.Set;

public interface RyaService {
    QueryResult<?> queryRdf(final String query,
                            final String authorizations,
                            final Optional<String> visibility,
                            final Boolean infer,
                            final Boolean noOutput);
}
