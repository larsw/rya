package org.apache.rya.web2;

import org.apache.rya.web2.controllers.RootHandler;
import org.apache.rya.web2.controllers.SparqlHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


//@Configuration
//public class Router {
//
//    private final RootHandler rootHandler;
//    private final SparqlHandler sparqlHandler;
//
//    public Router(RootHandler rootHandler, SparqlHandler sparqlHandler) {
//
//        this.rootHandler = rootHandler;
//        this.sparqlHandler = sparqlHandler;
//    }
//
//    @Bean
//    public RouterFunction<ServerResponse> routeRoot() {
//
//        return route(GET("/")
//                        .and(accept(APPLICATION_JSON)
//                        .or( accept(APPLICATION_XML))),
//                        rootHandler::handle)
//                .andRoute(POST("/sparql").or(GET("/sparql"))
//                        .and(accept(APPLICATION_JSON)
//                         .or(accept(APPLICATION_XML))),
//                        sparqlHandler::handle);
////                .andRoute(POST("/sparql")
////                        .and(accept(APPLICATION_JSON))
////                        .or(accept(APPLICATION_XML)),
////                        sparqlHandler::handle)
////                .andOther(route(path("*"), this::errorHandler));
//
//    }
//
//    private Mono<ServerResponse> errorHandler(ServerRequest serverRequest) {
//        return ServerResponse.notFound().build();
//    }
//}