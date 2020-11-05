package org.apache.rya.web2.services;

public class InvalidQueryException extends Throwable {
    public InvalidQueryException(String messageInclQuery) {
        super(messageInclQuery);
    }
}
