ALTER TABLE "order"
ADD COLUMN IF NOT EXISTS payment_status VARCHAR(10) NOT NULL DEFAULT 'UNPAID'
CHECK (payment_status IN ('PAID', 'UNPAID'));

CREATE INDEX IF NOT EXISTS idx_order_payment_status ON "order"(payment_status);

UPDATE "order" SET payment_status = 'UNPAID' WHERE payment_status IS NULL;
