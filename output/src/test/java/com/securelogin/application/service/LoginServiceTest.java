package com.securelogin.application.service;

import com.securelogin.domain.exception.AuthenticationException;
import com.securelogin.domain.model.User;
import com.securelogin.domain.port.outbound.JwtPort;
import com.securelogin.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * Unit tests for LoginService.
 */
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtPort jwtPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    private User verifiedUser;

    @BeforeEach
    void setUp() {
        verifiedUser = User.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .emailVerified(true)
                .build();
    }

    @Test
    @DisplayName("login returns JWT token for valid credentials and verified email")
    void login_validCredentials_returnsToken() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches("password123", verifiedUser.getPasswordHash())).thenReturn(true);
        when(jwtPort.generateToken(verifiedUser)).thenReturn("jwt.token.here");

        String token = loginService.login("user@example.com", "password123");

        assertThat(token).isEqualTo("jwt.token.here");
    }

    @Test
    @DisplayName("login throws AuthenticationException when user not found")
    void login_userNotFound_throwsAuthenticationException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login("unknown@example.com", "password"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("login throws AuthenticationException when password is wrong")
    void login_wrongPassword_throwsAuthenticationException() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(verifiedUser));
        when(passwordEncoder.matches("wrongpassword", verifiedUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> loginService.login("user@example.com", "wrongpassword"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Invalid credentials");
    }

    @Test
    @DisplayName("login throws AuthenticationException when email is not verified")
    void login_emailNotVerified_throwsAuthenticationException() {
        User unverifiedUser = User.builder()
                .id(UUID.randomUUID())
                .email("unverified@example.com")
                .passwordHash("$2a$10$hashedpassword")
                .emailVerified(false)
                .build();

        when(userRepository.findByEmail("unverified@example.com")).thenReturn(Optional.of(unverifiedUser));
        when(passwordEncoder.matches("password123", unverifiedUser.getPasswordHash())).thenReturn(true);

        assertThatThrownBy(() -> loginService.login("unverified@example.com", "password123"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("Email address has not been verified");
    }
}
