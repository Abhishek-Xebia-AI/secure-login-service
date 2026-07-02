package com.securelogin.domain.port.inbound;

import com.securelogin.domain.model.User;

/**
 * Inbound port: use-case for authenticating a user and issuing a JWT.
 */
public interface LoginUseCase {

    /**
     * Authenticates the user with the given credentials.
     *
     * @param email    the user's email address
     * @param password the user's raw password
     * @return a signed JWT access token
     */
    String login(String email, String password);
}
