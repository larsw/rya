package org.apache.rya.web2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

public class PrincipalClaimsProviderImpl implements PrincipalClaimsProvider {

    // Defaults to the 'authorizations' claim, which can be a comma-separated string or an array of strings.
    @Value("${rya.sparql.api.authorizationsClaim:authorizations}")
    private String authorizationsClaimName;

    private final Principal principal;

    public PrincipalClaimsProviderImpl(Principal principal) {

        this.principal = principal;
    }

    public Set<String> getAuthorizations() {
        JwtAuthenticationToken token = (JwtAuthenticationToken)principal;
        Object claim = token.getTokenAttributes().get(authorizationsClaimName);
        if (claim == null) return Collections.emptySet();

        if (claim instanceof String) {
            String stringClaim = (String)claim;
            return Arrays.stream(stringClaim.split(",")).collect(Collectors.toSet());
        }
        else if (claim instanceof List<?>) {
            List<String> listOfAuthorizations = (List<String>)claim;
            return new HashSet<>(listOfAuthorizations);
        }
        throw new UnsupportedOperationException("Unknown claim type: " + claim.getClass().getName());
    }
}
