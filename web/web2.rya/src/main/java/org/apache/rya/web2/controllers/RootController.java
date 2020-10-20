package org.apache.rya.web2.controllers;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import org.apache.rya.web2.models.RootModel;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public Mono<RootModel> get() {
        return Mono.just(new RootModel());
    }
}

