package org.apache.rya.web2.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RyaAuthorizationsProviderTests {

    @Test
    public void testXxx() {
        JwtAuthenticationToken mockedToken = mock(JwtAuthenticationToken.class);
        when(mockedToken.getTokenAttributes()).thenReturn(Collections.singletonMap("keywords", "foo,bar,baz"));

        RyaAuthorizer sut = new RyaAuthorizer();
        ReflectionTestUtils.setField(sut, "authorizationsClaim", "keywords", String.class);

        Optional<Set<String>> authorizations = sut.authorize(Optional.empty());
        Assertions.assertTrue(authorizations.isPresent());
    }
}
