package com.securelogin.domain.port.outbound;

import com.securelogin.domain.model.EmailVerificationToken;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port: repository contract for EmailVerificationToken persistence.
 */
public interface EmailVerificationTokenRepository {

    /**
     * Finds a token by its string value.
     *
     * @param token the token string
     * @return an Optional containing the token entity if found
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Persists a token entity (create or update).
     *
     * @param token the token to save
     * @return the saved token
     */
    EmailVerificationToken save(EmailVerificationToken token);
}
