--Création de l'utilisateur mini_dish_db_manager
CREATE USER mini_dish_db_manager WITH PASSWORD '1234';

--Création de la base de données mini_dish_db
CREATE DATABASE mini_dish_db
    WITH OWNER = mini_dish_db_manager
    ENCODING = 'UTF8'
    LC_COLLATE = 'fr_FR.UTF-8'
    LC_CTYPE = 'fr_FR.UTF-8'
    TEMPLATE = template0;

--Attribution de tous les privilèges pour l'utilisateur
GRANT ALL PRIVILEGES ON DATABASE mini_dish_db TO mini_dish_db_manager;

--Privilèges par défaut pour les futures tables et séquences
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO mini_dish_db_manager;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO mini_dish_db_manager;

--Création des types ENUM
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE category AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

--Création de la table Dish
CREATE TABLE Dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type NOT NULL
);

--Création de la table Ingredient
CREATE TABLE Ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    category category NOT NULL,
    id_dish INTEGER REFERENCES Dish(id) ON DELETE CASCADE
);

-- Insertion des données de test (celles de ton énoncé)
INSERT INTO Dish (name, dish_type) VALUES
('Salade fraîche', 'START'),
('Poulet grillé', 'MAIN'),
('Riz aux légumes', 'MAIN'),
('Gâteau au chocolat', 'DESSERT'),
('Salade de fruits', 'DESSERT');

INSERT INTO Ingredient (name, price, category, id_dish) VALUES
('Laitue', 800.00, 'VEGETABLE', 1),
('Tomate', 600.00, 'VEGETABLE', 1),
('Poulet', 4500.00, 'ANIMAL', 2),
('Chocolat', 3000.00, 'OTHER', 4),
('Beurre', 2500.00, 'DAIRY', 4);

-- Vérification rapide
SELECT 'Tables créées et données insérées !' AS status;
