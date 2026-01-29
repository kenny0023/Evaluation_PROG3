ALTER TABLE Ingredient
ADD COLUMN IF NOT EXISTS required_quantity NUMERIC(10, 2);

UPDATE Ingredient SET required_quantity = 1 WHERE name = 'Laitue';
UPDATE Ingredient SET required_quantity = 2 WHERE name = 'Tomate';
UPDATE Ingredient SET required_quantity = 0.5 WHERE name = 'Poulet';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Chocolat';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Beurre';

UPDATE dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE dish SET selling_price = NULL WHERE id = 3;
UPDATE dish SET selling_price = 8000.00 WHERE id = 4;
UPDATE dish SET selling_price = NULL WHERE id = 5;

UPDATE ingredient SET price = 4500.00 WHERE name ILIKE '%Poulet%';
UPDATE dish_ingredient SET quantity_required = 1.00 WHERE id_dish = 2
 AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Poulet%');

DELETE FROM dish_ingredient
WHERE id_dish = 2
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Poulet%');

ALTER TABLE Dish
ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10, 2);


INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit)
VALUES (
    2,
    (SELECT id FROM ingredient WHERE name ILIKE '%Poulet%'),
    1.00,
    'KG'
);

DELETE FROM dish_ingredient
WHERE id_dish = 4;

INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
(4, 4, 0.30, 'KG'),
(4, 5, 0.20, 'KG');

SELECT
    di.id,
    di.id_dish,
    d.name AS plat,
    di.id_ingredient,
    i.name AS ingredient,
    i.price,
    di.quantity_required,
    di.unit,
    (i.price * di.quantity_required) AS ligne_cout
FROM dish_ingredient di
JOIN dish d ON di.id_dish = d.id
JOIN ingredient i ON di.id_ingredient = i.id
WHERE di.id_dish = 4
ORDER BY di.id;
