CREATE TABLE app_users (
                           id BIGSERIAL PRIMARY KEY,
                           email VARCHAR(255) NOT NULL,
                           password_hash VARCHAR(255) NOT NULL,
                           full_name VARCHAR(150) NOT NULL,
                           role VARCHAR(30) NOT NULL,
                           enabled BOOLEAN NOT NULL DEFAULT TRUE,
                           created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                           CONSTRAINT chk_app_users_role CHECK (role IN ('ADMIN', 'MANAGER', 'PARTNER'))
);

CREATE UNIQUE INDEX ux_app_users_email_lower ON app_users (LOWER(email));


CREATE TABLE partner_profiles (
                                  id BIGSERIAL PRIMARY KEY,
                                  user_id BIGINT NOT NULL UNIQUE,
                                  company_name VARCHAR(255),
                                  contact_person VARCHAR(150) NOT NULL,
                                  phone VARCHAR(30),
                                  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_partner_profiles_user
                                      FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,

                                  CONSTRAINT chk_partner_profiles_status
                                      CHECK (status IN ('PENDING', 'ACTIVE', 'BLOCKED'))
);

CREATE INDEX ix_partner_profiles_status ON partner_profiles(status);


CREATE TABLE clients (
                         id BIGSERIAL PRIMARY KEY,
                         full_name VARCHAR(150) NOT NULL,
                         phone VARCHAR(30) NOT NULL,
                         email VARCHAR(255),
                         source VARCHAR(100),
                         partner_id BIGINT,
                         assigned_manager_id BIGINT,
                         created_by_id BIGINT NOT NULL,
                         archived BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         CONSTRAINT fk_clients_partner
                             FOREIGN KEY (partner_id) REFERENCES partner_profiles(id) ON DELETE SET NULL,

                         CONSTRAINT fk_clients_assigned_manager
                             FOREIGN KEY (assigned_manager_id) REFERENCES app_users(id) ON DELETE SET NULL,

                         CONSTRAINT fk_clients_created_by
                             FOREIGN KEY (created_by_id) REFERENCES app_users(id) ON DELETE RESTRICT
);

CREATE INDEX ix_clients_partner_id ON clients(partner_id);
CREATE INDEX ix_clients_assigned_manager_id ON clients(assigned_manager_id);
CREATE INDEX ix_clients_phone ON clients(phone);
CREATE INDEX ix_clients_created_at ON clients(created_at);


CREATE TABLE deal_statuses (
                               id BIGSERIAL PRIMARY KEY,
                               code VARCHAR(50) NOT NULL UNIQUE,
                               name VARCHAR(100) NOT NULL,
                               sort_order INT NOT NULL UNIQUE,
                               final_status BOOLEAN NOT NULL DEFAULT FALSE,
                               active BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO deal_statuses (code, name, sort_order, final_status)
VALUES
    ('NEW', 'Новая', 10, FALSE),
    ('CONTACTED', 'Контакт установлен', 20, FALSE),
    ('PRESENTATION', 'Презентация проведена', 30, FALSE),
    ('RESERVED', 'Бронь', 40, FALSE),
    ('CONTRACT', 'Договор', 50, FALSE),
    ('WON', 'Успешно закрыта', 60, TRUE),
    ('LOST', 'Потеряна', 70, TRUE);


CREATE TABLE deals (
                       id BIGSERIAL PRIMARY KEY,
                       client_id BIGINT NOT NULL,
                       partner_id BIGINT,
                       assigned_manager_id BIGINT,
                       status_id BIGINT NOT NULL,
                       title VARCHAR(200) NOT NULL,
                       property_name VARCHAR(200),
                       budget NUMERIC(15, 2),
                       amount NUMERIC(15, 2),
                       created_by_id BIGINT NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       closed_at TIMESTAMPTZ,

                       CONSTRAINT fk_deals_client
                           FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,

                       CONSTRAINT fk_deals_partner
                           FOREIGN KEY (partner_id) REFERENCES partner_profiles(id) ON DELETE SET NULL,

                       CONSTRAINT fk_deals_assigned_manager
                           FOREIGN KEY (assigned_manager_id) REFERENCES app_users(id) ON DELETE SET NULL,

                       CONSTRAINT fk_deals_status
                           FOREIGN KEY (status_id) REFERENCES deal_statuses(id) ON DELETE RESTRICT,

                       CONSTRAINT fk_deals_created_by
                           FOREIGN KEY (created_by_id) REFERENCES app_users(id) ON DELETE RESTRICT,

                       CONSTRAINT chk_deals_budget CHECK (budget IS NULL OR budget >= 0),
                       CONSTRAINT chk_deals_amount CHECK (amount IS NULL OR amount >= 0)
);

CREATE INDEX ix_deals_client_id ON deals(client_id);
CREATE INDEX ix_deals_partner_id ON deals(partner_id);
CREATE INDEX ix_deals_status_id ON deals(status_id);
CREATE INDEX ix_deals_assigned_manager_id ON deals(assigned_manager_id);
CREATE INDEX ix_deals_created_at ON deals(created_at);


CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          client_id BIGINT,
                          deal_id BIGINT,
                          author_id BIGINT NOT NULL,
                          text TEXT NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                          CONSTRAINT fk_comments_client
                              FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE,

                          CONSTRAINT fk_comments_deal
                              FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE CASCADE,

                          CONSTRAINT fk_comments_author
                              FOREIGN KEY (author_id) REFERENCES app_users(id) ON DELETE RESTRICT,

                          CONSTRAINT chk_comments_target
                              CHECK (
                                  (client_id IS NOT NULL AND deal_id IS NULL)
                                      OR
                                  (client_id IS NULL AND deal_id IS NOT NULL)
                                  )
);

CREATE INDEX ix_comments_client_id ON comments(client_id);
CREATE INDEX ix_comments_deal_id ON comments(deal_id);
CREATE INDEX ix_comments_author_id ON comments(author_id);
CREATE INDEX ix_comments_created_at ON comments(created_at);


CREATE TABLE audit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            entity_type VARCHAR(50) NOT NULL,
                            entity_id BIGINT NOT NULL,
                            action VARCHAR(80) NOT NULL,
                            field_name VARCHAR(100),
                            old_value TEXT,
                            new_value TEXT,
                            actor_id BIGINT,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_audit_logs_actor
                                FOREIGN KEY (actor_id) REFERENCES app_users(id) ON DELETE SET NULL
);

CREATE INDEX ix_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX ix_audit_logs_actor_id ON audit_logs(actor_id);
CREATE INDEX ix_audit_logs_created_at ON audit_logs(created_at);


CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL,
                                token_hash VARCHAR(255) NOT NULL UNIQUE,
                                expires_at TIMESTAMPTZ NOT NULL,
                                revoked BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_refresh_tokens_user
                                    FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE
);

CREATE INDEX ix_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX ix_refresh_tokens_expires_at ON refresh_tokens(expires_at);


CREATE TABLE commission_rules (
                                  id BIGSERIAL PRIMARY KEY,
                                  partner_id BIGINT NOT NULL,
                                  percent NUMERIC(5, 2) NOT NULL,
                                  active BOOLEAN NOT NULL DEFAULT TRUE,
                                  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                  CONSTRAINT fk_commission_rules_partner
                                      FOREIGN KEY (partner_id) REFERENCES partner_profiles(id) ON DELETE CASCADE,

                                  CONSTRAINT chk_commission_rules_percent
                                      CHECK (percent >= 0 AND percent <= 100)
);

CREATE INDEX ix_commission_rules_partner_id ON commission_rules(partner_id);
CREATE UNIQUE INDEX ux_commission_rules_active_partner
    ON commission_rules(partner_id)
    WHERE active = TRUE;


CREATE TABLE partner_commissions (
                                     id BIGSERIAL PRIMARY KEY,
                                     partner_id BIGINT NOT NULL,
                                     deal_id BIGINT NOT NULL UNIQUE,
                                     amount NUMERIC(15, 2) NOT NULL,
                                     percent NUMERIC(5, 2) NOT NULL,
                                     status VARCHAR(30) NOT NULL DEFAULT 'CALCULATED',
                                     calculated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     approved_at TIMESTAMPTZ,
                                     paid_at TIMESTAMPTZ,

                                     CONSTRAINT fk_partner_commissions_partner
                                         FOREIGN KEY (partner_id) REFERENCES partner_profiles(id) ON DELETE RESTRICT,

                                     CONSTRAINT fk_partner_commissions_deal
                                         FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE RESTRICT,

                                     CONSTRAINT chk_partner_commissions_amount
                                         CHECK (amount >= 0),

                                     CONSTRAINT chk_partner_commissions_percent
                                         CHECK (percent >= 0 AND percent <= 100),

                                     CONSTRAINT chk_partner_commissions_status
                                         CHECK (status IN ('CALCULATED', 'APPROVED', 'PAID', 'CANCELLED'))
);

CREATE INDEX ix_partner_commissions_partner_id ON partner_commissions(partner_id);
CREATE INDEX ix_partner_commissions_status ON partner_commissions(status);


CREATE TABLE billing_events (
                                id BIGSERIAL PRIMARY KEY,
                                deal_id BIGINT,
                                event_type VARCHAR(80) NOT NULL,
                                payload JSONB NOT NULL,
                                processed BOOLEAN NOT NULL DEFAULT FALSE,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                processed_at TIMESTAMPTZ,

                                CONSTRAINT fk_billing_events_deal
                                    FOREIGN KEY (deal_id) REFERENCES deals(id) ON DELETE SET NULL
);

CREATE INDEX ix_billing_events_deal_id ON billing_events(deal_id);
CREATE INDEX ix_billing_events_event_type ON billing_events(event_type);
CREATE INDEX ix_billing_events_processed ON billing_events(processed);
CREATE INDEX ix_billing_events_created_at ON billing_events(created_at);