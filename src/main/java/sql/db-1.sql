CREATE TABLE Ingredient (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    category category NOT NULL,
    id_dish INTEGER REFERENCES Dish(id) ON DELETE CASCADE
);