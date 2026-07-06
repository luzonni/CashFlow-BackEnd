-- ============================================
-- V1__init_schema.sql
-- Schema inicial do CashFlow
-- ============================================

-- USERS
CREATE TABLE users
(
    id                            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    username                      VARCHAR(255),
    email                         VARCHAR(255) UNIQUE NOT NULL,
    password_hash                 VARCHAR(255)        NOT NULL,
    birthday                      DATE,
    email_verified                BOOLEAN             NOT NULL DEFAULT false,
    verification_token            VARCHAR(255),
    verification_token_expires_at TIMESTAMP,
    created_at                    TIMESTAMP           NOT NULL DEFAULT now()
);

-- GROUP_CATEGORIES
CREATE TABLE group_categories
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255),
    description TEXT,
    deleted     BOOLEAN   NOT NULL DEFAULT false,
    user_id     UUID REFERENCES users (id),
    create_at   TIMESTAMP NOT NULL DEFAULT now()
);

-- CATEGORIES
CREATE TABLE categories
(
    id        BIGSERIAL PRIMARY KEY,
    user_id   UUID REFERENCES users (id),
    group_id  BIGINT REFERENCES group_categories (id),
    color     VARCHAR(20),
    name      VARCHAR(255),
    deleted   BOOLEAN   NOT NULL DEFAULT false,
    create_at TIMESTAMP NOT NULL DEFAULT now()
);

-- PAYMENT_METHOD
CREATE TABLE payment_method
(
    id         BIGSERIAL PRIMARY KEY,
    user_id    UUID REFERENCES users (id),
    color      VARCHAR(20),
    name       VARCHAR(255),
    deleted    BOOLEAN   NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- TRANSACTIONS
CREATE TABLE transactions
(
    id                UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    user_id           UUID REFERENCES users (id),
    payment_method_id BIGINT REFERENCES payment_method (id),
    category_id       BIGINT REFERENCES categories (id),
    amount            DECIMAL(15, 2) NOT NULL,
    description       TEXT,
    type              VARCHAR(20)    NOT NULL, -- enum TransactionType (STRING)
    currency          VARCHAR(10),
    state             VARCHAR(20),             -- enum TransactionState (STRING)
    transaction_date  DATE,
    created_at        TIMESTAMP      NOT NULL DEFAULT now()
);

-- RECURRENCE
CREATE TABLE recurrence
(
    id                UUID PRIMARY KEY        DEFAULT gen_random_uuid(),
    user_id           UUID REFERENCES users (id),
    category_id       BIGINT REFERENCES categories (id),
    payment_method_id BIGINT REFERENCES payment_method (id),
    type              VARCHAR(20)    NOT NULL,                  -- enum TransactionType (STRING)
    amount            DECIMAL(15, 2) NOT NULL,
    name              VARCHAR(255),
    description       TEXT,
    frequency         VARCHAR(20),                              -- enum Scheduling (STRING)
    interval_value    INTEGER,
    max_occurrences   INTEGER,
    currency          VARCHAR(10),
    timezone          VARCHAR(50),
    status            VARCHAR(20)    NOT NULL DEFAULT 'ACTIVE', -- enum RecurrenceStatus (STRING)
    create_at         TIMESTAMP      NOT NULL DEFAULT now()
);

-- RECURRENCE_EXECUTION_RECORDS
CREATE TABLE recurrence_execution_records
(
    id                UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
    recurrence_id     UUID REFERENCES recurrence (id),
    transaction_id    UUID REFERENCES transactions (id),
    amount            DECIMAL(15, 2),
    executed_at       TIMESTAMP,
    scheduled_to      DATE,
    occurrence_number INTEGER,
    status            VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- enum RecurrenceRecordStatus (STRING)
    created_at        TIMESTAMP   NOT NULL DEFAULT now()
);

-- USER_SETTINGS (PK compartilhada com users via @MapsId)
CREATE TABLE user_settings
(
    user_id  UUID PRIMARY KEY REFERENCES users (id),
    theme    VARCHAR(16) NOT NULL DEFAULT 'system',
    locale   VARCHAR(16) NOT NULL DEFAULT 'en-US',
    currency VARCHAR(8)  NOT NULL DEFAULT 'USD'
);

-- REFRESH_TOKENS (auto-referência via replaced_by_token_id)
CREATE TABLE refresh_tokens
(
    id                   UUID PRIMARY KEY   DEFAULT gen_random_uuid(),
    user_id              UUID REFERENCES users (id),
    token_hash           VARCHAR(255),
    expires_at           TIMESTAMP,
    revoked              BOOLEAN   NOT NULL DEFAULT false,
    created_at           TIMESTAMP NOT NULL DEFAULT now(),
    revoked_at           TIMESTAMP,
    replaced_by_token_id UUID REFERENCES refresh_tokens (id)
);

-- Índices úteis para consultas mais frequentes
CREATE INDEX idx_transactions_user_id ON transactions (user_id);
CREATE INDEX idx_transactions_date ON transactions (transaction_date);
CREATE INDEX idx_categories_user_id ON categories (user_id);
CREATE INDEX idx_recurrence_user_id ON recurrence (user_id);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);