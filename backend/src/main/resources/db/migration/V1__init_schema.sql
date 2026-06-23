CREATE TABLE app_info (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          version VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO app_info (name, version)
VALUES ('PartnerFlow CRM', '0.0.1');