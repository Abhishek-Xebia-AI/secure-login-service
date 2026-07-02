-- V2__create_email_verification_tokens_table.sql
CREATE TABLE email_verification_tokens (
    id         UUID         NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token      VARCHAR(512) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ  NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_evt_token   ON email_verification_tokens (token);
CREATE INDEX idx_evt_user_id ON email_verification_tokens (user_id);
