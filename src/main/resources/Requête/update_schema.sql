ALTER TABLE Ingredient
ADD COLUMN IF NOT EXISTS required_quantity NUMERIC(10, 2);

UPDATE Ingredient SET required_quantity = 1 WHERE name = 'Laitue';
UPDATE Ingredient SET required_quantity = 2 WHERE name = 'Tomate';
UPDATE Ingredient SET required_quantity = 0.5 WHERE name = 'Poulet';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Chocolat';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Beurre';

ALTER TABLE Dish
ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10, 2);

--Ajouts et modifications des colonnes existantes
ALTER TABLE dish ADD COLUMN IF NOT EXISTS selling_price NUMERIC(15,2);
ALTER TABLE "order" ALTER COLUMN total_ttc TYPE NUMERIC(15,2);

ALTER TABLE ingredient DROP COLUMN IF EXISTS id_dish;
ALTER TABLE ingredient DROP COLUMN IF EXISTS required_quantity;
ALTER TABLE ingredient DROP COLUMN IF EXISTS quantity;

--prix et quantités
UPDATE dish SET selling_price = 3500.00 WHERE name ILIKE '%Salade Fraîche%';
UPDATE dish SET selling_price = 12000.00 WHERE name ILIKE '%Poulet%';
UPDATE dish SET selling_price = 8000.00 WHERE name ILIKE '%Gâteau au Chocolat%';
UPDATE dish SET selling_price = NULL WHERE name ILIKE '%Riz%' OR name ILIKE '%Fruits%';

UPDATE ingredient SET price = 800.00 WHERE name ILIKE '%Laitue%';
UPDATE ingredient SET price = 600.00 WHERE name ILIKE '%Tomate%';
UPDATE ingredient SET price = 4500.00 WHERE name ILIKE '%Poulet%';
UPDATE ingredient SET price = 3000.00 WHERE name ILIKE '%Chocolat%';
UPDATE ingredient SET price = 2500.00 WHERE name ILIKE '%Beurre%';

--Quantités dans dish_ingredient
UPDATE dish_ingredient
SET quantity_required = 1.00, unit = 'PIECE'::unit_enum
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Salade Fraîche%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Laitue%');

UPDATE dish_ingredient
SET quantity_required = 0.25, unit = 'KG'::unit_enum
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Salade Fraîche%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Tomate%');

UPDATE dish_ingredient
SET quantity_required = 0.50, unit = 'KG'::unit_enum
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Poulet%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Poulet%');

UPDATE dish_ingredient
SET quantity_required = 0.30, unit = 'KG'::unit_enum
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Gâteau%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Chocolat%');

UPDATE dish_ingredient
SET quantity_required = 0.20, unit = 'KG'::unit_enum
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Gâteau%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Beurre%');

--Nettoyage doublons
DELETE FROM dish_ingredient
WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Gâteau%')
  AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Chocolat%')
  AND quantity_required = 0.30
  AND ctid NOT IN (
      SELECT MIN(ctid)
      FROM dish_ingredient
      WHERE id_dish = (SELECT id FROM dish WHERE name ILIKE '%Gâteau%')
        AND id_ingredient = (SELECT id FROM ingredient WHERE name ILIKE '%Chocolat%')
      GROUP BY id_dish, id_ingredient, quantity_required, unit
  );

--Vérification finale de tout
SELECT 
    d.name AS plat,
    i.name AS ingredient,
    di.quantity_required,
    di.unit,
    (i.price * di.quantity_required) AS cout_ligne
FROM dish_ingredient di
JOIN dish d ON di.id_dish = d.id
JOIN ingredient i ON di.id_ingredient = i.id
ORDER BY d.name, i.name;
