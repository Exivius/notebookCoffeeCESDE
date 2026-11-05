-- Campos usados por el catálogo administrativo y el inventario.
ALTER TABLE products
    ADD COLUMN product_description VARCHAR(500) NOT NULL DEFAULT '',
    ADD COLUMN stock INT NULL DEFAULT NULL;
