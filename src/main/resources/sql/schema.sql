-- Création des types ENUM
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE category AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE unit_enum AS ENUM ('PIECE', 'KG', 'L', 'ML', 'G', 'CL');

-- Table Dish
CREATE TABLE Dish (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    dish_type dish_type NOT NULL
);

-- Table Ingredient
CREATE TABLE Ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    category category NOT NULL,
    id_dish INTEGER,
    FOREIGN KEY (id_dish) REFERENCES Dish(id) ON DELETE SET NULL
);
