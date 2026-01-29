CREATE TYPE movement_type AS ENUM ('IN', 'OUT');

CREATE TABLE stock_movement (
    id                  SERIAL PRIMARY KEY,
    id_ingredient       INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity            NUMERIC(10, 2) NOT NULL,
    unit                unit_enum NOT NULL,           -- ou unit si tu as changé le nom
    type                movement_type NOT NULL,        -- IN ou OUT
    creation_datetime   TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE stock_movement
ADD CONSTRAINT chk_quantity_positive CHECK (quantity > 0);

INSERT INTO stock_movement (id_ingredient, quantity, unit, type, creation_datetime) VALUES
(1, 5.0,  'KG', 'IN',  '2024-01-05 10:00:00'),
(1, 0.2,  'KG', 'OUT', '2024-01-06 14:00:00'),
(2, 4.0,  'KG', 'IN',  '2024-01-05 10:00:00'),
(2, 0.15, 'KG', 'OUT', '2024-01-06 14:00:00'),
(3, 10.0, 'KG', 'IN',  '2024-01-05 10:00:00'),
(3, 1.0,  'KG', 'OUT', '2024-01-06 14:00:00'),
(4, 3.0,  'KG', 'IN',  '2024-01-05 10:00:00'),
(4, 0.3,  'KG', 'OUT', '2024-01-06 14:00:00'),
(5, 2.5,  'KG', 'IN',  '2024-01-05 10:00:00'),
(5, 0.2,  'KG', 'OUT', '2024-01-06 14:00:00');