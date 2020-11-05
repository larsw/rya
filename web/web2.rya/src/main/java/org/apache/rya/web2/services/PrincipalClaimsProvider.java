package org.apache.rya.web2.services;

import java.util.Set;

public interface PrincipalClaimsProvider {
    Set<String> getAuthorizations();
}
