-- =========================
-- Extensão do UUID
-- =========================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";


-- =========================
-- User
-- =========================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    birthday DATE not NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- =========================
-- Grupo de categorias do Usuáirio
-- =========================
CREATE TABLE group_categories (
    id serial PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    deleted boolean not null default false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unique(user_id, name)
);


-- =========================
-- Categorias do Usuário
-- =========================
CREATE TABLE categories (
    id serial PRIMARY KEY,
    color varchar(7) not null,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    group_id serial not null references group_categories(id) on delete restrict,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    deleted boolean not null default false,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, group_id, name)
);


-- =========================
-- Transaction
-- =========================
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    description TEXT,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    transaction_date DATE NOT NULL,
    state VARCHAR(10) not null check (state in ('PENDING', 'CONFIRM', 'CANCELLED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- Recurrence
-- =========================
create table recurrences (
	id UUID primary key default uuid_generate_v4(),
	name varchar(50) not null,
	description varchar(120),
	amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
	user_id UUID not null references users(id) on delete cascade,
	category_id UUID not null references categories(id) on delete restrict,
	scheduling varchar(10) not null check (scheduling in('frequency', 'day_of_month', 'use_last_day')),
	max_occurrences varchar(50) not null,
	status VARCHAR(10) not null check (status in ('ACTIVE', 'ENDED', 'CANCELED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- Execution_Record
-- =========================

create table recurrence_execution_records (
	id UUID primary key default uuid_generate_v4(),
	recurrence_id UUID references recurrence(id) on delete cascade,
	transaction_id UUID references transactions(id) on delete restrict,
	amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
	release_date timestamp not null,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- Refresh Token
-- =========================
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMP,
    replaced_by_token_id UUID,
    device_info TEXT,
    ip_address TEXT
);
CREATE INDEX idx_refresh_token_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_token_hash ON refresh_tokens(token_hash);


-- =========================
-- ROLES
-- =========================
CREATE TABLE roles (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

-- =========================
-- USER_ROLES (N:N)
-- =========================
CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id INT NOT NULL,

    PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

-- =========================
-- Index
-- =========================
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);


