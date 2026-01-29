CREATE TABLE IF NOT EXISTS "order" (
    id                SERIAL PRIMARY KEY,
    reference         VARCHAR(255) UNIQUE NOT NULL,
    creation_datetime TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total_ttc         NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dish_order (
    id          SERIAL PRIMARY KEY,
    id_order    INTEGER NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    id_dish     INTEGER NOT NULL REFERENCES dish(id) ON DELETE RESTRICT,
    quantity    INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX IF NOT EXISTS idx_order_reference ON "order"(reference);
CREATE INDEX IF NOT EXISTS idx_dish_order_id_order ON dish_order(id_order);
CREATE INDEX IF NOT EXISTS idx_stock_movement_id_ingredient ON stock_movement(id_ingredient);

