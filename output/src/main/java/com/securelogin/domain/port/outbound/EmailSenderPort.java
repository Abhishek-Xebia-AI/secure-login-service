package com.securelogin.domain.port.outbound;

/**
 * Outbound port: contract for sending emails.
 */
public interface EmailSenderPort {

    /**
     * Sends an email verification message to the given address.
     *
     * @param toEmail           the recipient's email address
     * @param verificationToken the token to include in the verification link
     */
    void sendVerificationEmail(String toEmail, String verificationToken);
}
