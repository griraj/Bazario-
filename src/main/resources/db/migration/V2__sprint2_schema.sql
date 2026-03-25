-- ============================================================
-- V2__sprint2_schema.sql
-- Bazario Sprint 2 – Transaction & Order Management System
-- ============================================================

-- ─── Carts ──────────────────────────────────────────────────
CREATE TABLE carts (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL UNIQUE REFERENCES users(id),
    created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP  NOT NULL DEFAULT NOW()
);

-- ─── Cart Items ─────────────────────────────────────────────
CREATE TABLE cart_items (
    id         BIGSERIAL    PRIMARY KEY,
    cart_id    BIGINT       NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id BIGINT       NOT NULL REFERENCES products(id),
    quantity   INTEGER      NOT NULL DEFAULT 1,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_cart_item_quantity CHECK (quantity > 0),
    UNIQUE(cart_id, product_id)
);

-- ─── Orders ─────────────────────────────────────────────────
CREATE TABLE orders (
    id             BIGSERIAL      PRIMARY KEY,
    user_id        BIGINT         NOT NULL REFERENCES users(id),
    total_amount   NUMERIC(12, 2) NOT NULL,
    status         VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    tracking_info  VARCHAR(255),
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_order_status CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    CONSTRAINT chk_order_total  CHECK (total_amount >= 0)
);

CREATE INDEX idx_orders_user_id ON orders(user_id);

-- ─── Order Items ────────────────────────────────────────────
CREATE TABLE order_items (
    id             BIGSERIAL      PRIMARY KEY,
    order_id       BIGINT         NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id     BIGINT         NOT NULL REFERENCES products(id),
    storefront_id  BIGINT         NOT NULL REFERENCES storefronts(id),
    price_at_time  NUMERIC(12, 2) NOT NULL,
    quantity       INTEGER        NOT NULL,
    status         VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_order_item_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_item_price CHECK (price_at_time >= 0),
    CONSTRAINT chk_order_item_status CHECK (status IN ('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'))
);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_storefront_id ON order_items(storefront_id);

-- ─── Reviews ────────────────────────────────────────────────
CREATE TABLE reviews (
    id         BIGSERIAL    PRIMARY KEY,
    product_id BIGINT       NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    user_id    BIGINT       NOT NULL REFERENCES users(id),
    rating     INTEGER      NOT NULL,
    comment    TEXT,
    created_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_review_rating CHECK (rating >= 1 AND rating <= 5),
    UNIQUE(product_id, user_id)
);

CREATE INDEX idx_reviews_product_id ON reviews(product_id);
