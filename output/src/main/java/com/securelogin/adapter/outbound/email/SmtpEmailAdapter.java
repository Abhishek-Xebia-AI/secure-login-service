package com.securelogin.adapter.outbound.email;

import com.securelogin.domain.port.outbound.EmailSenderPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Email adapter implementing the EmailSenderPort outbound port using Spring Mail.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;

    @Value("${app.email.from:noreply@securelogin.com}")
    private String fromAddress;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {
        String verificationLink = baseUrl + "/api/v1/auth/verify-email?token=" + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toEmail);
        message.setSubject("Verify your email address");
        message.setText("Please verify your email by clicking the link below:\n\n" + verificationLink
                + "\n\nThis link expires in 24 hours.");

        try {
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
