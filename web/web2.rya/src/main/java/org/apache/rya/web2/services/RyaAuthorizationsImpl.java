package org.apache.rya.web2.services;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class RyaAuthorizationsImpl implements RyaAuthorizations {
    private final Set<String> authorizations;

    public RyaAuthorizationsImpl(String commaSeparatedAuthorizations) {
        if (commaSeparatedAuthorizations == null) throw new IllegalArgumentException("commaSeparatedAuthorizations");
        this.authorizations =
                Arrays.stream(commaSeparatedAuthorizations.split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAuthorizations() {
        return authorizations;
    }
}
