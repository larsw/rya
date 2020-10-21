package org.apache.rya.web2.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Principal;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RyaAuthorizationsProviderTests {

    @Test
    public void testXxx() {
        RyaAuthorizationsProvider sut = new RyaAuthorizationsProvider();
        ReflectionTestUtils.setField(sut, "authorizationsClaim", "keywords", String.class);

        JwtAuthenticationToken mockedToken = mock(JwtAuthenticationToken.class);
        when(mockedToken.getTokenAttributes()).thenReturn(Collections.singletonMap("keywords", "foo,bar,baz"));

        RyaAuthorizations ryaAuthorizations = sut.getRyaAuthorizationsProvider(mockedToken);
        Assertions.assertNotNull(ryaAuthorizations);
    }
}
