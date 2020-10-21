package org.apache.rya.web2.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class RyaAuthorizationsProviderImpl implements RyaAuthorizationsProvider {
    @Value("rya.sparql.api.authorizationsClaim")
    private String authorizationsClaim;

    private final List<String> authorizations;


    public RyaAuthorizationsProviderImpl(@CurrentSecurityContext(expression = "authentication.principal")Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken)principal;
        String authorizations = (String)token.getTokenAttributes().getOrDefault(authorizationsClaim, "");

        this.authorizations = Arrays.stream(authorizations.split(",")).map(x -> x.trim()).collect(Collectors.toList());
    }

    @Override
    public List<String> getAuthorizations() {
        return authorizations;
    }
}
