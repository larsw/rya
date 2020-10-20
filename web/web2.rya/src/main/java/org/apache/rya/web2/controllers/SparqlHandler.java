package org.apache.rya.web2.controllers;

import org.apache.rya.web2.models.SparqlQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sparql")
public class SparqlHandler {

    @PostMapping("")
    public Mono<SparqlQueryResponse> postSparqlQuery() {
//        if (request.method() != HttpMethod.POST) {
//            return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build();
//        }
//        return ServerResponse.ok().build();
        return Mono.just(new SparqlQueryResponse());
    }
}
