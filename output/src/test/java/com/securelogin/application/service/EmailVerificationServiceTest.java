package com.securelogin.application.service;

import com.securelogin.domain.exception.InvalidTokenException;
import com.securelogin.domain.model.EmailVerificationToken;
import com.securelogin.domain.model.User;
import com.securelogin.domain.port.outbound.EmailVerificationTokenRepository;
import com.securelogin.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for EmailVerificationService.
 */
@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private UUID userId;
    private User user;
    private EmailVerificationToken validToken;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .email("user@example.com")
                .emailVerified(false)
                .build();

        validToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token("valid-token-abc123")
                .expiresAt(Instant.now().plusSeconds(3600))
                .used(false)
                .build();
    }

    @Test
    @DisplayName("verifyEmail marks user email as verified for a valid token")
    void verifyEmail_validToken_marksEmailVerified() {
        when(tokenRepository.findByToken("valid-token-abc123")).thenReturn(Optional.of(validToken));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenRepository.save(any(EmailVerificationToken.class))).thenReturn(validToken);

        emailVerificationService.verifyEmail("valid-token-abc123");

        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    @DisplayName("verifyEmail throws InvalidTokenException when token not found")
    void verifyEmail_tokenNotFound_throwsInvalidTokenException() {
        when(tokenRepository.findByToken("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("nonexistent"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("verifyEmail throws InvalidTokenException when token is expired")
    void verifyEmail_expiredToken_throwsInvalidTokenException() {
        EmailVerificationToken expiredToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token("expired-token")
                .expiresAt(Instant.now().minusSeconds(3600))
                .used(false)
                .build();

        when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("expired-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expired or already used");
    }

    @Test
    @DisplayName("verifyEmail throws InvalidTokenException when token is already used")
    void verifyEmail_alreadyUsedToken_throwsInvalidTokenException() {
        EmailVerificationToken usedToken = EmailVerificationToken.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .token("used-token")
                .expiresAt(Instant.now().plusSeconds(3600))
                .used(true)
                .build();

        when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(usedToken));

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("used-token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("expired or already used");
    }
}
