CREATE TABLE payments (
                          id BIGSERIAL PRIMARY KEY,
                          order_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          amount NUMERIC(10, 2) NOT NULL,

    -- The idempotency key: unique constraint is the safety net
    -- Even concurrent duplicate requests - DB ensures only one succeeds
                          idempotency_key VARCHAR(100) NOT NULL UNIQUE,

                          status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    -- Which payment gateway processed this (razorpay, stripe, etc.)
                          gateway VARCHAR(50),

    -- Gateway's own transaction ID - for reconciliation
                          gateway_transaction_id VARCHAR(200),

    -- Full gateway response stored as JSON for audit trail
                          gateway_response TEXT,

                          failure_reason VARCHAR(500),

                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Fast lookup by order (most common query: "what's the payment for order X?")
CREATE INDEX idx_payments_order_id ON payments(order_id);

-- Fast lookup by user (for payment history page)
CREATE INDEX idx_payments_user_id ON payments(user_id);

-- Fast lookup by status (for admin dashboard: all PENDING payments)
CREATE INDEX idx_payments_status ON payments(status);

-- Fast lookup by idempotency key (hit on every duplicate request check)
CREATE INDEX idx_payments_idempotency_key ON payments(idempotency_key);