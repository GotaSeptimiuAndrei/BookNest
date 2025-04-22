ALTER TABLE email_verification
    ADD COLUMN registration_type VARCHAR(20) NOT NULL,
    ADD COLUMN registration_payload TEXT NOT NULL;

CREATE INDEX idx_email_verification_lookup
    ON email_verification (email, verification_code, verified);