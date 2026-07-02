package com.securelogin.domain.port.outbound;

import com.securelogin.domain.model.User;

/**
 * Outbound port: contract for generating JWT tokens.
 */
public interface JwtPort {

    /**
     * Generates a signed JWT access token for the given user.
     *
     * @param user the authenticated user
     * @return a signed JWT string
     */
    String generateToken(User user);

    /**
     * Validates a JWT token and returns the subject (email).
     *
     * @param token the JWT string to validate
     * @return the subject claim (user email)
     */
    String validateTokenAndGetSubject(String token);
}
