-- Insertion des plats
INSERT INTO Dish (name, dish_type) VALUES
('Salade Fraîche', 'START'),
('Poulet Rôti', 'MAIN'),
('Gâteau au chocolat', 'DESSERT');

-- Insertion des ingrédients
INSERT INTO Ingredient (name, price, category, id_dish) VALUES
('Laitue', 800.0, 'VEGETABLE', 1),
('Tomate', 600.0, 'VEGETABLE', 1),
('Poulet', 3500.0, 'ANIMAL', 2),
('Chocolat', 2500.0, 'OTHER', 3),
('Beurre', 1800.0, 'DAIRY', 3);
