package org.apache.rya.web2.services;

import org.springframework.stereotype.Component;

import java.util.List;

public interface RyaAuthorizationsProvider {
    List<String> getAuthorizations();
}
