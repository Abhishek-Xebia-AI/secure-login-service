package com.securelogin.domain.port.outbound;

import com.securelogin.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port: repository contract for User persistence.
 */
public interface UserRepository {

    /**
     * Finds a user by their email address.
     *
     * @param email the email to search for
     * @return an Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their unique identifier.
     *
     * @param id the user's UUID
     * @return an Optional containing the user if found
     */
    Optional<User> findById(UUID id);

    /**
     * Persists a user entity (create or update).
     *
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);
}
