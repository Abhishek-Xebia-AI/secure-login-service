package com.securelogin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain entity representing an email verification token.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken {

    private UUID id;
    private UUID userId;
    private String token;
    private Instant expiresAt;
    private boolean used;

    /**
     * Returns true if the token has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Returns true if the token is still valid (not used and not expired).
     */
    public boolean isValid() {
        return !used && !isExpired();
    }
}
