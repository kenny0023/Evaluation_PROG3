-- Création des types ENUM
CREATE TYPE dish_type AS ENUM ('START', 'MAIN', 'DESSERT');
CREATE TYPE category AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');
CREATE TYPE IF NOT EXISTS unit_enum AS ENUM ('PIECE', 'KG', 'L', 'ML', 'G', 'CL');
CREATE TYPE IF NOT EXISTS movement_type AS ENUM ('IN', 'OUT');

-- Table dish
CREATE TABLE dish (
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    dish_type     dish_type NOT NULL,
    selling_price NUMERIC(12, 2)
);

-- Table ingredient
CREATE TABLE ingredient (
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL UNIQUE,
    price    NUMERIC(10, 2) NOT NULL,
    category category NOT NULL
);

-- Table liaison plat-ingrédient
CREATE TABLE dish_ingredient (
    id                SERIAL PRIMARY KEY,
    id_dish           INTEGER NOT NULL REFERENCES dish(id) ON DELETE CASCADE,
    id_ingredient     INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity_required NUMERIC(10, 2) NOT NULL,
    unit              unit_enum NOT NULL,
    UNIQUE (id_dish, id_ingredient)
);

-- Table mouvements de stock
CREATE TABLE stock_movement (
    id                SERIAL PRIMARY KEY,
    id_ingredient     INTEGER NOT NULL REFERENCES ingredient(id) ON DELETE CASCADE,
    quantity          NUMERIC(10, 2) NOT NULL CHECK (quantity > 0),
    unit              unit_enum NOT NULL,
    type              movement_type NOT NULL,
    creation_datetime TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tables commandes (Annexe 2)
CREATE TABLE "order" (
    id                SERIAL PRIMARY KEY,
    reference         VARCHAR(255) UNIQUE NOT NULL,
    creation_datetime TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    total_ttc         NUMERIC(15, 2) NOT NULL DEFAULT 0   -- augmenté pour éviter limites
);

CREATE TABLE dish_order (
    id          SERIAL PRIMARY KEY,
    id_order    INTEGER NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
    id_dish     INTEGER NOT NULL REFERENCES dish(id) ON DELETE RESTRICT,
    quantity    INTEGER NOT NULL CHECK (quantity > 0)
);

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_order_reference ON "order"(reference);
CREATE INDEX IF NOT EXISTS idx_dish_order_id_order ON dish_order(id_order);
CREATE INDEX IF NOT EXISTS idx_stock_id_ing ON stock_movement(id_ingredient);
