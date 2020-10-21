package org.apache.rya.web2.controllers;

import org.apache.rya.web2.services.RyaAuthorizationsProvider;
import org.apache.rya.web2.services.RyaAuthorizationsProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.apache.rya.web2.models.SparqlQueryResponse;

import java.security.Principal;
import java.time.Duration;
import java.time.temporal.TemporalUnit;

@RestController
@RequestMapping("/sparql")
public class SparqlController {
    Logger logger = LoggerFactory.getLogger(SparqlController.class);

    // TODO check out https://www.baeldung.com/spring-webflux-404

    @Autowired
    private RyaAuthorizationsProvider authorizationsProvider;

    @PostMapping
    public Mono<SparqlQueryResponse> postSparqlQuery(ServerWebExchange swe) {
        ServerHttpRequest req = swe.getRequest();

        String query = req.getQueryParams().getFirst("query");
        String visibility = req.getQueryParams().getFirst("visibility");

        String acceptHeader = req.getHeaders().getFirst(HttpHeaders.ACCEPT);
        logger.debug("Accept header: " + acceptHeader);
        // https://stackoverflow.com/questions/41462060/project-reactor-timeout-handling
        //
        //
        return Mono.just(new SparqlQueryResponse());
    }
}
