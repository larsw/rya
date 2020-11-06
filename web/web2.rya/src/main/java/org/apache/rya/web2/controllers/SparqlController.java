package org.apache.rya.web2.controllers;

import org.apache.rya.web2.services.PrincipalClaimsProvider;
import org.apache.rya.web2.services.PrincipalClaimsProviderImpl;
import org.apache.rya.web2.services.RyaAuthorizations;
import org.apache.rya.web2.services.RyaAuthorizationsImpl;
import org.apache.rya.web2.services.RyaService;
import org.eclipse.rdf4j.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin
@RestController
@RequestMapping("/sparql")
public class SparqlController {
    Logger logger = LoggerFactory.getLogger(SparqlController.class);


    private final RyaService ryaService;

    public SparqlController(RyaService ryaService) {

        this.ryaService = ryaService;
    }

    final static Pattern VisibilityPattern = Pattern.compile("[a-z0-9]+", Pattern.CASE_INSENSITIVE);

    @PostMapping
    public ResponseEntity<QueryResult<?>> postSparqlQuery(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "visibility", required = false) Optional<String> visibilityParam,
            @RequestParam(value = "infer", required = false) Optional<String> inferParam,
            @RequestParam(value = "nooutput", required = false) Optional<String> noOutputParam,
            @RequestHeader(value = "Accept", required = false) String acceptHeader,
            Principal principal) {

        PrincipalClaimsProvider principalClaimsProvider = new PrincipalClaimsProviderImpl(principal);

        Set<String> authorizationsSet = principalClaimsProvider.getAuthorizations();
        Set<String> visibilitiesSet = getVisibilities(visibilityParam);

        if (!authorizationsSet.containsAll(visibilitiesSet)) {
            logger.error("Unauthorized access: tried to use the following visibilities: {}, while only authorized for: {}",
                    visibilitiesSet,
                    authorizationsSet
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String authorizationsSeparatedByComma = String.join(",", authorizationsSet);

        logger.debug("query: {}, visibility: {}, infer: {}, accept: {}, authorizations: {}",
                query,
                visibilitiesSet,
                inferParam,
                acceptHeader,
                authorizationsSet);

        Boolean infer = inferParam.map(Boolean::parseBoolean).orElse(false);
        Boolean noOutput = noOutputParam.map(Boolean::parseBoolean).orElse(false);

        try {
            return ResponseEntity.ok(ryaService.queryRdf(query, authorizationsSeparatedByComma, visibilityParam, infer, noOutput));
        } catch (final Exception e) { // TODO differentiate between possible 4xx and 5xx exceptions.
            logger.error("Could not perform query: {}", query); // TODO include claim(s) identifying the caller.
            return ResponseEntity.badRequest().build();
        }
    }

    private Set<String> getVisibilities(Optional<String> visibility) {
        return visibility.map(x -> {
            Matcher matcher = VisibilityPattern.matcher(x);
            Set<String> parts = new HashSet<>();
            while(matcher.find()) {
                parts.add(matcher.group());
            }
            return parts;
        }).orElse(Collections.emptySet());
    }


}
