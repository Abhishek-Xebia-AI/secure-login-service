package com.securelogin.application.service;

import com.securelogin.domain.exception.InvalidTokenException;
import com.securelogin.domain.exception.UserNotFoundException;
import com.securelogin.domain.model.EmailVerificationToken;
import com.securelogin.domain.model.User;
import com.securelogin.domain.port.inbound.VerifyEmailUseCase;
import com.securelogin.domain.port.outbound.EmailVerificationTokenRepository;
import com.securelogin.domain.port.outbound.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service implementing the VerifyEmailUseCase.
 * Validates the token, marks the user's email as verified, and invalidates the token.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService implements VerifyEmailUseCase {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void verifyEmail(String token) {
        log.debug("Email verification attempt with token: {}", token);

        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Verification token not found"));

        if (!verificationToken.isValid()) {
            throw new InvalidTokenException("Verification token is expired or already used");
        }

        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found for token"));

        user.setEmailVerified(true);
        userRepository.save(user);

        verificationToken.setUsed(true);
        tokenRepository.save(verificationToken);

        log.info("Email verified for user id={}", user.getId());
    }
}
