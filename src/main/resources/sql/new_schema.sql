SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'dish' AND column_name = 'dish_type';

CREATE TYPE unit_enum AS ENUM ('PIECE', 'KG', 'L', 'ML', 'G');

CREATE TABLE dish_ingredient (
  id SERIAL PRIMARY KEY,
  id_dish INTEGER NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
  id_ingredient INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
  quantity_required NUMERIC(10, 2) NOT NULL,
  unit unit_enum NOT NULL
);

ALTER TABLE dish ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10,2);

ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;
ALTER TABLE ingredient DROP COLUMN IF EXISTS quantity;

SELECT * FROM dish;

INSERT INTO ingredient (name, price, category) VALUES
('Laitue', 800.00, 'VEGETABLE'),
('Tomate', 600.00, 'VEGETABLE'),
('Poulet', 4500.00, 'ANIMAL'),
('Chocolat', 3000.00, 'OTHER'),
('Beurre', 2500.00, 'DAIRY');

INSERT INTO dish (name, dish_type, selling_price) VALUES 
('Salade fraiche', 'START', 3500.00),
('Poulet grille', 'MAIN', 12000.00),
('Riz aux legumes', 'MAIN', NULL),
('Gateau au chocolat', 'DESSERT', 8000.00),
('Salade de fruits', 'DESSERT', NULL);

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES 
(1, 1, 0.20, 'KG'),
(1, 2, 0.15, 'KG'),
(2, 3, 1.00, 'KG'),
(4, 4, 0.30, 'KG'),
(4, 5, 0.20, 'KG');

SELECT * FROM ingredient;

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES (4, 4, 0.30, 'KG');
