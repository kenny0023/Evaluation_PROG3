UPDATE DishIngredient
SET required_quantity = 1.0, unit = 'PIECE'::unit_enum
WHERE id_dish = 1 AND id_ingredient = (SELECT id FROM Ingredient WHERE name = 'Laitue');

UPDATE DishIngredient
SET required_quantity = 0.25, unit = 'KG'::unit_enum
WHERE id_dish = 1 AND id_ingredient = (SELECT id FROM Ingredient WHERE name = 'Tomate');

UPDATE DishIngredient
SET required_quantity = 0.5, unit = 'KG'::unit_enum
WHERE id_dish = 2 AND id_ingredient = (SELECT id FROM Ingredient WHERE name = 'Poulet');

UPDATE DishIngredient
SET required_quantity = 0.2, unit = 'KG'::unit_enum
WHERE id_dish = 3 AND id_ingredient = (SELECT id FROM Ingredient WHERE name = 'Chocolat');

UPDATE DishIngredient
SET required_quantity = 0.1, unit = 'KG'::unit_enum
WHERE id_dish = 3 AND id_ingredient = (SELECT id FROM Ingredient WHERE name = 'Beurre');

-- Mise à jour des prix de vente des plats
UPDATE Dish SET selling_price = 12000.0 WHERE id = 2;
UPDATE Dish SET selling_price = 5000.0 WHERE id = 3;
