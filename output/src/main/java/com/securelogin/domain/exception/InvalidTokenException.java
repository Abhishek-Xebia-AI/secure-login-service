package com.securelogin.domain.exception;

/**
 * Thrown when an email verification token is invalid or expired.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
