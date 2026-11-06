-- Actualiza únicamente la cuenta administrativa de ejemplo si conserva uno
-- de los correos del seed anterior. No cambia contraseñas ni otras cuentas.
SET @seeded_admin_id = (
    SELECT MIN(id) FROM person
    WHERE UPPER(rol) = 'ADMIN'
      AND email IN ('admin@coffee.com', 'admin@cofee.com')
);
SET @target_admin_exists = (
    SELECT COUNT(*) FROM person WHERE email = 'admin@admin.com'
);
UPDATE person
SET email = 'admin@admin.com'
WHERE id = @seeded_admin_id AND @target_admin_exists = 0;
