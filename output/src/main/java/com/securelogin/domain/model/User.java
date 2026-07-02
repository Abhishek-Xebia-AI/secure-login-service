package com.securelogin.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Core domain entity representing a user in the system.
 * This is a pure domain object with no framework dependencies.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String email;
    private String passwordHash;
    private boolean emailVerified;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Returns true if the user account is fully active (email verified).
     */
    public boolean isActive() {
        return emailVerified;
    }
}
