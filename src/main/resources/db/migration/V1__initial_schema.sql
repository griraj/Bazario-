-- ============================================================
-- V1__initial_schema.sql
-- Bazario Sprint 1 – User Management & Product Catalog
-- ============================================================



-- ─── Users ──────────────────────────────────────────────────
CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    role          VARCHAR(50)  NOT NULL,
    enabled       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);

-- ─── Storefronts ────────────────────────────────────────────
CREATE TABLE storefronts (
    id          BIGSERIAL    PRIMARY KEY,
    vendor_id   BIGINT       NOT NULL UNIQUE REFERENCES users(id),
    name        VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    status      VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_storefront_status CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED'))
);

-- ─── Categories ─────────────────────────────────────────────
CREATE TABLE categories (
    id        BIGSERIAL    PRIMARY KEY,
    name      VARCHAR(100) NOT NULL UNIQUE,
    slug      VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO categories (name, slug) VALUES
    ('Electronics',      'electronics'),
    ('Fashion',          'fashion'),
    ('Home & Garden',    'home-garden'),
    ('Sports',           'sports'),
    ('Books',            'books'),
    ('Health & Beauty',  'health-beauty'),
    ('Toys & Games',     'toys-games'),
    ('Automotive',       'automotive');

-- ─── Products ───────────────────────────────────────────────
CREATE TABLE products (
    id             BIGSERIAL       PRIMARY KEY,
    storefront_id  BIGINT          NOT NULL REFERENCES storefronts(id),
    category_id    BIGINT          NOT NULL REFERENCES categories(id),
    name           VARCHAR(255)    NOT NULL,
    description    TEXT,
    price          NUMERIC(12, 2)  NOT NULL,
    stock_quantity INTEGER         NOT NULL DEFAULT 0,
    sku            VARCHAR(100)    UNIQUE,
    status         VARCHAR(20)     NOT NULL DEFAULT 'DRAFT',
    created_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_product_price  CHECK (price >= 0),
    CONSTRAINT chk_product_stock  CHECK (stock_quantity >= 0),
    CONSTRAINT chk_product_status CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_products_category  ON products(category_id);
CREATE INDEX idx_products_storefront ON products(storefront_id);
CREATE INDEX idx_products_name_search ON products USING gin(to_tsvector('english', name));
