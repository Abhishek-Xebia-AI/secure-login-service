package com.securelogin.adapter.outbound.persistence;

import com.securelogin.domain.model.EmailVerificationToken;
import com.securelogin.domain.port.outbound.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Persistence adapter implementing the EmailVerificationTokenRepository outbound port.
 */
@Component
@RequiredArgsConstructor
public class EmailVerificationTokenRepositoryAdapter implements EmailVerificationTokenRepository {

    private final SpringDataEmailVerificationTokenRepository springDataRepository;

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return springDataRepository.findByToken(token)
                .map(EmailVerificationTokenJpaEntity::toDomain);
    }

    @Override
    public EmailVerificationToken save(EmailVerificationToken token) {
        EmailVerificationTokenJpaEntity entity = EmailVerificationTokenJpaEntity.fromDomain(token);
        return springDataRepository.save(entity).toDomain();
    }
}
