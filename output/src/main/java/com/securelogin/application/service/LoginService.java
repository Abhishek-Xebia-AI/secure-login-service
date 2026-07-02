package com.securelogin.application.service;

import com.securelogin.domain.exception.AuthenticationException;
import com.securelogin.domain.model.User;
import com.securelogin.domain.port.inbound.LoginUseCase;
import com.securelogin.domain.port.outbound.JwtPort;
import com.securelogin.domain.port.outbound.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Application service implementing the LoginUseCase.
 * Orchestrates authentication: credential validation, email-verification check, JWT issuance.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;
    private final JwtPort jwtPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String login(String email, String password) {
        log.debug("Login attempt for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }

        if (!user.isEmailVerified()) {
            throw new AuthenticationException("Email address has not been verified. Please check your inbox.");
        }

        String token = jwtPort.generateToken(user);
        log.info("Successful login for user id={}", user.getId());
        return token;
    }
}
