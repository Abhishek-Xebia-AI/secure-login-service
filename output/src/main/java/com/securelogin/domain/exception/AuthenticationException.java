package com.securelogin.domain.exception;

/**
 * Thrown when authentication fails (invalid credentials, unverified email, etc.).
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
