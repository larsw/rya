package org.apache.rya.web2.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RyaAuthorizationsImpl implements RyaAuthorizations {
    private final List<String> authorizations;

    public RyaAuthorizationsImpl(String commaSeparatedAuthorizations) {
        if (commaSeparatedAuthorizations == null) throw new IllegalArgumentException("commaSeparatedAuthorizations");
        this.authorizations =
                Arrays.stream(commaSeparatedAuthorizations.split(","))
                        .map(String::trim)
                        .collect(Collectors.toList());
    }

    @Override
    public List<String> getAuthorizations() {
        return authorizations;
    }
}
