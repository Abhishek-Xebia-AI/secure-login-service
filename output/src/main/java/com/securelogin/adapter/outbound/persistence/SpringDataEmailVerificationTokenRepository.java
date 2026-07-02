package com.securelogin.adapter.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for EmailVerificationTokenJpaEntity.
 */
public interface SpringDataEmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationTokenJpaEntity, UUID> {

    Optional<EmailVerificationTokenJpaEntity> findByToken(String token);
}
