package org.apache.rya.web2.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;

//@Component
public class RyaAuthorizationsProvider {

    @Value("rya.sparql.api.authorizationsClaim")
    private String authorizationsClaim;

//    @Bean
//    @RequestScope
    public RyaAuthorizations getRyaAuthorizationsProvider(@CurrentSecurityContext(expression = "authentication.principal")Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken)principal;
        String authorizationClaimValue = (String)token.getTokenAttributes().getOrDefault(authorizationsClaim, "");
        return new RyaAuthorizationsImpl(authorizationClaimValue);
    }
}
