ALTER TABLE Ingredient
ADD COLUMN IF NOT EXISTS required_quantity NUMERIC(10, 2);

UPDATE Ingredient SET required_quantity = 1 WHERE name = 'Laitue';
UPDATE Ingredient SET required_quantity = 2 WHERE name = 'Tomate';
UPDATE Ingredient SET required_quantity = 0.5 WHERE name = 'Poulet';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Chocolat';
UPDATE Ingredient SET required_quantity = NULL WHERE name = 'Beurre';

ALTER TABLE Dish
ADD COLUMN IF NOT EXISTS selling_price NUMERIC(10, 2);
