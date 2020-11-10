package org.apache.rya.web2.controllers;

import org.apache.rya.web2.services.RyaAuthorizer;
import org.apache.rya.web2.services.RyaService;
import org.eclipse.rdf4j.query.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Provider;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/sparql")
public class SparqlController {
    Logger logger = LoggerFactory.getLogger(SparqlController.class);


    private final RyaService ryaService;
    private final Provider<RyaAuthorizer> ryaAuthorizationsProvider;

    public SparqlController(RyaService ryaService, Provider<RyaAuthorizer> ryaAuthorizationsProvider) {

        this.ryaService = ryaService;
        this.ryaAuthorizationsProvider = ryaAuthorizationsProvider;
    }



    @PostMapping
    public ResponseEntity<QueryResult<?>> postSparqlQuery(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "visibility", required = false) Optional<String> visibilityParam,
            @RequestParam(name = "infer", required = false) Optional<String> inferParam,
            @RequestParam(name = "nooutput", required = false) Optional<String> noOutputParam,
            @RequestHeader(name = "Accept", required = false) String acceptHeader) {

        Optional<Set<String>> authorizationsSet = ryaAuthorizationsProvider.get().authorize(visibilityParam);
        if (!authorizationsSet.isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String authorizationsSeparatedByComma = String.join(",", authorizationsSet.get());

        logger.debug("query: {}, visibility: {}, infer: {}, accept: {}, authorizations: {}",
                query,
                visibilityParam,
                inferParam,
                acceptHeader,
                authorizationsSet);

        Boolean infer = inferParam.map(Boolean::parseBoolean).orElse(false);
        Boolean noOutput = noOutputParam.map(Boolean::parseBoolean).orElse(false);

        try {
            QueryResult<?> result = ryaService.queryRdf(query, authorizationsSeparatedByComma, visibilityParam, infer, noOutput);
            return ResponseEntity.ok(result);
        } catch (final Exception e) { // TODO differentiate between possible 4xx and 5xx exceptions.
            logger.error("Could not perform query: {}", query); // TODO include claim(s) identifying the caller.
            return ResponseEntity.badRequest().build();
        }
    }


}
