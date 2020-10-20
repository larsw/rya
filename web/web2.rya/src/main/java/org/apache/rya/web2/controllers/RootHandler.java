package org.apache.rya.web2.controllers;

import org.apache.rya.web2.models.RootModel;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RootHandler {
    public Mono<ServerResponse> handle(ServerRequest request) {
        return Mono.from(ServerResponse.ok()
                .bodyValue(new RootModel()));
    }
}


