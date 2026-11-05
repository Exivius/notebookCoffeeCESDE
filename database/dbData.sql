USE notebook_coffee_cesde;

-- 1. Población de la tabla 'person' (Usuarios y Administradores)
INSERT INTO person (name, last_name, email, phone, password, state, rol, city, area) VALUES
('Felipe', 'Correa', 'admin@admin.com', '3001234567', 'admin123', 'Activo', 'Admin', NULL, 'Administración Cafetería'),
('Valentina', 'Restrepo', 'user1@coffee.com', '3109876543', 'user123', 'Activo', 'User', 'Medellín', NULL),
('Alejandro', 'Gómez', 'user2@coffee.com', '3205554433', 'user123', 'Activo', 'User', 'Medellín', NULL),
('Camila', 'López', 'user3@coffee.com', '3154443322', 'user123', 'Inactivo', 'User', 'Medellín', NULL);

-- 2. Población de la tabla 'place' (Puntos de atención/entrega)
INSERT INTO place (place_name, place_type) VALUES
('Centro', 'Punto de Venta'),
('Bello', 'Punto de Entrega'),
('Rionegro', 'Punto de Entrega'),
('Bogotá', 'Punto de Entrega');

-- 3. Población de la tabla 'products' (Bebidas, snacks y papelería)
INSERT INTO products (product_name, product_price, product_type) VALUES
('Café Americano', 2500.00, 'Bebida Caliente'),
('Cappuccino', 4500.00, 'Bebida Caliente'),
('Shot de Bienestar (Naranja, Limón, Cúrcuma, Jengibre)', 6000.00, 'Bebida Fría'),
('Empanada de Carne', 3000.00, 'Snack Salado'),
('Pastel de Pollo', 3500.00, 'Snack Salado'),
('Cuaderno Argollado 100 hojas', 12000.00, 'Papelería'),
('Bolígrafo Negro', 1500.00, 'Papelería');

-- 4. Población de la tabla 'shopping_cart' (Carritos con el total precalculado)
INSERT INTO shopping_cart (total) VALUES
(8000.00),  -- Carrito 1: Cappuccino (4500) + Pastel de Pollo (3500)
(14500.00), -- Carrito 2: Shot de Bienestar (6000) + Cuaderno (12000)? No, Americano (2500) + Cuaderno (12000)
(6000.00);  -- Carrito 3: 2x Empanadas (3000 c/u)

-- 5. Población de la tabla 'cart_item' (Detalle de productos en cada carrito)
-- Items del Carrito 1
INSERT INTO cart_item (cart_id, product_id, quantity, price_by_quantity, total) VALUES
(1, 2, 1, 4500.00, 4500.00), -- 1 Cappuccino
(1, 5, 1, 3500.00, 3500.00); -- 1 Pastel de Pollo

-- Items del Carrito 2
INSERT INTO cart_item (cart_id, product_id, quantity, price_by_quantity, total) VALUES
(2, 1, 1, 2500.00, 2500.00), -- 1 Café Americano
(2, 6, 1, 12000.00, 12000.00); -- 1 Cuaderno Argollado

-- Items del Carrito 3
INSERT INTO cart_item (cart_id, product_id, quantity, price_by_quantity, total) VALUES
(3, 4, 2, 3000.00, 6000.00); -- 2 Empanadas

-- 6. Población de la tabla 'order' (Asociación de usuarios, lugares y carritos)
INSERT INTO `order` (place_id, user_id, cart_id) VALUES
(1, 2, 1), -- Valentina compra en Cafetería Central (Carrito 1)
(4, 3, 2), -- Alejandro pide entrega a la Biblioteca (Carrito 2)
(2, 4, 3); -- Camila pide entrega al Bloque A (Carrito 3)

-- 7. Población de la tabla 'payment_methods'
INSERT INTO payment_methods (method_name) VALUES
('Efectivo'),
('Tarjeta de Crédito / Débito'),
('Nequi'),
('Bancolombia A la Mano');

-- 8. Población de la tabla 'payment' (Transacciones vinculadas a las órdenes)
INSERT INTO payment (payment_type, order_id, method_id) VALUES
('Pago Presencial', 1, 1), -- Orden 1 pagada en Efectivo
('Pago Digital', 2, 3),    -- Orden 2 pagada con Nequi
('Pago Digital', 3, 4);    -- Orden 3 pagada con Bancolombia A la Mano;
