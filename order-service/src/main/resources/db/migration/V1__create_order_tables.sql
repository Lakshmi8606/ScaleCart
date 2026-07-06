-- Cart: temporary storage of items before checkout
CREATE TABLE carts (
                       id BIGSERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL UNIQUE,  -- one cart per user
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cart items: each product in the cart
CREATE TABLE cart_items (
                            id BIGSERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
                            product_id BIGINT NOT NULL,      -- references product-service, no FK (microservices!)
                            product_name VARCHAR(200) NOT NULL,  -- denormalized: snapshot at time of adding
                            price NUMERIC(10, 2) NOT NULL,       -- denormalized: price at time of adding
                            quantity INTEGER NOT NULL DEFAULT 1,
                            UNIQUE(cart_id, product_id)          -- no duplicate products in same cart
);

-- Order status enum values: PENDING → CONFIRMED → SHIPPED → DELIVERED / CANCELLED
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                        total_amount NUMERIC(10, 2) NOT NULL,
                        shipping_address TEXT NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Order items: permanent snapshot of what was ordered
CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id BIGINT NOT NULL,
                             product_name VARCHAR(200) NOT NULL,  -- snapshot: even if product deleted later
                             price NUMERIC(10, 2) NOT NULL,       -- snapshot: even if price changes later
                             quantity INTEGER NOT NULL
);

CREATE INDEX idx_carts_user_id ON carts(user_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);