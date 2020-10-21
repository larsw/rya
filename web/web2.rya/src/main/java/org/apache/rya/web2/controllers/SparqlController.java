package org.apache.rya.web2.controllers;

import org.apache.rya.web2.services.RyaAuthorizations;
import org.apache.rya.web2.services.RyaAuthorizationsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@CrossOrigin
@RestController
@RequestMapping("/sparql")
public class SparqlController {
    Logger logger = LoggerFactory.getLogger(SparqlController.class);

    @Value("${rya.sparql.api.authorizationsClaim}")
    private String authorizationsClaim;

    @PostMapping
    public void postSparqlQuery(HttpServletRequest request, HttpServletResponse response, Principal principal) {
        String query = request.getParameter("query");
        String visibility = request.getParameter("visibility");
        String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);

        RyaAuthorizations authorizations = getRyaAuthorizations(principal);
        logger.debug("query: " + query);
        logger.debug("visibility: " + visibility);
        for (String authz : authorizations.getAuthorizations()) {
            logger.debug("authz: " + authz);
        }
        response.setStatus(HttpStatus.OK.value());
    }

    private RyaAuthorizations getRyaAuthorizations(Principal principal) {
        JwtAuthenticationToken token = (JwtAuthenticationToken)principal;
        String authorizationClaimValue = (String)token.getTokenAttributes().getOrDefault(authorizationsClaim, "");
        return new RyaAuthorizationsImpl(authorizationClaimValue);
    }
}
