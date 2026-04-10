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
-- Categorias Padrão
-- =========================
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    parent_id INT REFERENCES categories(id) ON DELETE SET NULL
);


-- =========================
-- Categorias de Usuario
-- =========================
CREATE TABLE user_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    base_category_id INT REFERENCES categories(id) ON DELETE SET NULL,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(10) NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE(user_id, name)
);


-- =========================
-- Transactions
-- =========================
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_category_id UUID NOT NULL REFERENCES user_categories(id) ON DELETE RESTRICT,
    amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    description TEXT,
    transaction_date DATE NOT NULL,
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
-- PERMISSIONS
-- =========================
CREATE TABLE permissions (
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
-- ROLE_PERMISSIONS (N:N)
-- =========================
CREATE TABLE role_permissions (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,

    PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_perm_role
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_perm_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

-- =========================
-- Indices de Permissões (N:N)
-- =========================
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);


