package org.apache.rya.web2.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@RestController
@RequestMapping("/sparql")
public class SparqlController {
     Logger logger = LoggerFactory.getLogger(SparqlController.class);

  // TODO check out https://www.baeldung.com/spring-webflux-404

    @PostMapping
    public Mono<SparqlQueryResponse> postSparqlQuery(ServerWebExchange swe) {
        ServerHttpRequest req = swe.getRequest();
        String query = req.getQueryParams().getFirst("query");
        logger.debug("query: " + query); 	
	String acceptHeader = req.getHeaders().getFirst(HttpHeaders.ACCEPT);
	logger.debug("Accept header: " + acceptHeader); 
 // https://stackoverflow.com/questions/41462060/project-reactor-timeout-handling
 //
 //
        return Mono.just(new SparqlQueryResponse());
    }
}
