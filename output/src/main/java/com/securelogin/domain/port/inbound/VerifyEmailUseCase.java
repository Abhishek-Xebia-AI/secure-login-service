package com.securelogin.domain.port.inbound;

/**
 * Inbound port: use-case for verifying a user's email address.
 */
public interface VerifyEmailUseCase {

    /**
     * Verifies the user's email using the provided token.
     *
     * @param token the email verification token
     */
    void verifyEmail(String token);
}
