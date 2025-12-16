--Création de l'utilisateur mini_dish_db_manager
CREATE USER mini_dish_db_manager WITH PASSWORD 'mini_dish_password';

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
