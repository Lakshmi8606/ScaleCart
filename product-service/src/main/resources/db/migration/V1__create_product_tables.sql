CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            description TEXT,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(200) NOT NULL,
                          description TEXT,
                          price NUMERIC(10, 2) NOT NULL,
                          stock_quantity INTEGER NOT NULL DEFAULT 0,
                          category_id BIGINT NOT NULL REFERENCES categories(id),
                          image_url VARCHAR(500),
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index on category_id because "get all products in category X"
-- will be one of your most common queries
CREATE INDEX idx_products_category_id ON products(category_id);

-- Index on active because you'll almost always filter out inactive products
CREATE INDEX idx_products_active ON products(active);

-- Seed some categories so the app has data to test with
INSERT INTO categories (name, description) VALUES
                                               ('Electronics', 'Phones, laptops, gadgets'),
                                               ('Clothing', 'Shirts, pants, accessories'),
                                               ('Books', 'Fiction, non-fiction, textbooks');

-- Seed some products
INSERT INTO products (name, description, price, stock_quantity, category_id) VALUES
                                                                                 ('iPhone 15', 'Apple iPhone 15 128GB', 79999.00, 50, 1),
                                                                                 ('Samsung Galaxy S24', 'Samsung flagship phone', 74999.00, 30, 1),
                                                                                 ('Java Programming Book', 'Complete Java guide', 499.00, 100, 3);