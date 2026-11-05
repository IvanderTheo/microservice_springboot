ALTER TABLE payments
    ADD COLUMN order_id BIGINT NULL,
    ADD COLUMN user_id BIGINT NULL;

CREATE UNIQUE INDEX ux_payments_order_id ON payments(order_id);
