package com.securelogin.adapter.outbound.persistence;

import com.securelogin.domain.model.EmailVerificationToken;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for the email_verification_tokens table.
 */
@Entity
@Table(name = "email_verification_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationTokenJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    // --- Mapping helpers ---

    public static EmailVerificationTokenJpaEntity fromDomain(EmailVerificationToken domain) {
        return EmailVerificationTokenJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .token(domain.getToken())
                .expiresAt(domain.getExpiresAt())
                .used(domain.isUsed())
                .build();
    }

    public EmailVerificationToken toDomain() {
        return EmailVerificationToken.builder()
                .id(id)
                .userId(userId)
                .token(token)
                .expiresAt(expiresAt)
                .used(used)
                .build();
    }
}
