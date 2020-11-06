package org.apache.rya.web2.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

import org.apache.rya.web2.models.RootModel;

@RestController
@RequestMapping("/")
public class RootController {

    @GetMapping
    public @ResponseBody RootModel get() {
        return new RootModel();
    }
}

