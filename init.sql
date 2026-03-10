CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS changeables (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS category_items (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    category_id BIGINT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      time TIMESTAMP,
                                      status VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           food VARCHAR(255) NOT NULL,
    count INT NOT NULL,
    order_id BIGINT,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS order_items_changeables (
                                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                       order_item_id BIGINT NOT NULL,
                                                       price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE
    );

-- 🧠 BRAINROT DATA 🧠

-- Categories
INSERT INTO categories (name) VALUES
                                  ('Sigma Meals'),
                                  ('Gyatt Drinks'),
                                  ('Bussin Desserts'),
                                  ('Skibidi Snacks');

-- Changeables (doplňky)
INSERT INTO changeables (name, price, category_id) VALUES
                                                       ('Extra Rizz Sauce', 0.50, 1),
                                                       ('Ohio Water (nebezpečné)', 0.00, 2),
                                                       ('Edging Spice Level', 1.00, 1),
                                                       ('Fanum Tax (10% extra)', 2.50, 4),
                                                       ('Gigachad Portion +50%', 3.99, 3),
                                                       ('Mewing Cream', 0.75, 3),
                                                       ('NPC Mode (žádná zelenina)', 0.00, 1);


-- Category Items (jídla)
INSERT INTO category_items (name, price, category_id) VALUES
-- Sigma Meals
('Giga Burger (mogging edition)', 12.99, 1),
('Chicken Nuggets but fr fr no cap', 8.50, 1),
('Baby Gronk Pizza', 14.99, 1),
('Livvy Dunne Pasta Alfredo', 11.50, 1),

-- Gyatt Drinks
('Grimace Shake (RIP)', 4.99, 2),
('Literally Just Water (W)', 1.99, 2),
('Prime Hydration (Logan Paul tax)', 5.99, 2),
('Iced Coffee (hits different)', 3.50, 2),

-- Bussin Desserts
('Costco Guys Cake (boom!)', 6.99, 3),
('Ice Cream Sandwich (bussin)', 4.50, 3),
('Sussy Baka Brownies', 5.99, 3),
('Brain Freeze Deluxe', 7.50, 3),

-- Skibidi Snacks
('Cheese Balls (W snack)', 2.99, 4),
('Skibidi Toilet Fries', 3.99, 4),
('Goated Nachos', 5.50, 4),
('Locked In Wings (10ks)', 8.99, 4);
