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

-- Insertion plats (TD3)
INSERT INTO dish (name, dish_type, selling_price) VALUES
('Salade Fraîche',   'START',    3500.00),
('Poulet Rôti',      'MAIN',    12000.00),
('Riz aux Légumes',  'MAIN',       NULL),
('Gâteau au Chocolat','DESSERT', 8000.00),
('Salade de Fruits', 'DESSERT',    NULL)
ON CONFLICT DO NOTHING;

-- Liaison plat-ingrédient (TD3)
INSERT INTO dish_ingredient (id_dish, id_ingredient, quantity_required, unit) VALUES
(1, (SELECT id FROM ingredient WHERE name = 'Laitue'),   1.00, 'PIECE'),
(1, (SELECT id FROM ingredient WHERE name = 'Tomate'),   0.25, 'KG'),
(2, (SELECT id FROM ingredient WHERE name = 'Poulet'),   0.50, 'KG'),
(4, (SELECT id FROM ingredient WHERE name = 'Chocolat'), 0.30, 'KG'),
(4, (SELECT id FROM ingredient WHERE name = 'Beurre'),   0.20, 'KG')
ON CONFLICT (id_dish, id_ingredient) DO NOTHING;

-- Insertion ingrédients (TD3 + TD4)
INSERT INTO ingredient (name, price, category) VALUES
('Laitue',   800.00, 'VEGETABLE'),
('Tomate',   600.00, 'VEGETABLE'),
('Poulet',  4500.00, 'ANIMAL'),
('Chocolat', 3000.00, 'OTHER'),
('Beurre',   2500.00, 'DAIRY')
ON CONFLICT (name) DO UPDATE SET
    price = EXCLUDED.price,
    category = EXCLUDED.category;

-- Mouvements de stock (TD4)
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
(5, 0.2,  'KG', 'OUT', '2024-01-06 14:00:00')
ON CONFLICT DO NOTHING;