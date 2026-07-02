package com.securelogin.adapter.inbound.rest;

import com.securelogin.domain.port.inbound.VerifyEmailUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST adapter for the email verification use-case.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final VerifyEmailUseCase verifyEmailUseCase;

    /**
     * Verifies a user's email address using the provided token.
     *
     * @param token the verification token from the email link
     * @return 200 OK on success
     */
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        verifyEmailUseCase.verifyEmail(token);
        return ResponseEntity.ok().build();
    }
}
