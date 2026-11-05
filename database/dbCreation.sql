-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS notebook_coffee_cesde;
USE notebook_coffee_cesde;

-- 1. Tabla Unificada Person (Integra User y Admin)
CREATE TABLE person (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    state VARCHAR(10) NOT NULL,
    rol VARCHAR(50) NOT NULL, -- Determina si es 'User' o 'Admin'
    city VARCHAR(100),        -- Campo específico para el rol 'User'
    area VARCHAR(100)         -- Campo específico para el rol 'Admin'
);

-- 2. Tabla Place
CREATE TABLE place (
    place_id INT AUTO_INCREMENT PRIMARY KEY,
    place_name VARCHAR(100) NOT NULL,
    place_type VARCHAR(100) NOT NULL
);

-- 3. Tabla ShoppingCart
CREATE TABLE shopping_cart (
    cart_id INT AUTO_INCREMENT PRIMARY KEY,
    total FLOAT DEFAULT 0.0
);

-- 4. Tabla Order
CREATE TABLE `order` (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    place_id INT,
    user_id INT, -- Ahora apunta directamente a la tabla 'person'
    cart_id INT,
    FOREIGN KEY (place_id) REFERENCES place(place_id),
    FOREIGN KEY (user_id) REFERENCES person(id),
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(cart_id)
);

-- 5. Tabla Products
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(150) NOT NULL,
    product_price FLOAT NOT NULL,
    product_type VARCHAR(100) NOT NULL,
    product_description VARCHAR(500) NOT NULL DEFAULT '',
    stock INT NULL DEFAULT NULL
);

-- 6. Tabla Cart_Item
CREATE TABLE cart_item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price_by_quantity FLOAT NOT NULL,
    total FLOAT NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES shopping_cart(cart_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- 7. Tabla PaymentMethods
CREATE TABLE payment_methods (
    method_id INT AUTO_INCREMENT PRIMARY KEY,
    method_name VARCHAR(100) NOT NULL
);

-- 8. Tabla Payment
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_type VARCHAR(100) NOT NULL,
    order_id INT NOT NULL,
    method_id INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `order`(order_id),
    FOREIGN KEY (method_id) REFERENCES payment_methods(method_id)
);

SELECT * FROM person WHERE rol = 'Admin';
