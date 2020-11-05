package org.apache.rya.web2.services;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class RyaAuthorizationsImplTests {

    @Test
    public void testConstructorThrowWhenNoAuthorizationsAreProvided() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            RyaAuthorizationsImpl sut = new RyaAuthorizationsImpl(null);
        });
    }

    @Test
    public void testReturnsListOfAuthorizationsOnValidInput() {
        RyaAuthorizationsImpl sut = new RyaAuthorizationsImpl("foo,bar,baz");
        Set<String> authorizations = sut.getAuthorizations();
        Assertions.assertEquals(3, authorizations.size());
    }
}
