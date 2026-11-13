# Historias de usuario — noteBookCoffeeCESDE

**Sistema:** pedidos de cafetería y papelería CESDE
**Arquitectura:** hexagonal (dominio · puertos · adaptadores)
**Persistencia:** MySQL 8+ vía JDBC
**Interfaz:** consola (CLI)

## Convenciones

- **Prioridad:** Alta / Media / Baja.
- **Estado:** Implementada / Implementada con brecha / Pendiente.
- Formato: *Como \<actor\> quiero \<acción\> para \<beneficio\>*.
- Los criterios usados se marcan con `[x]`; las brechas conocidas con `[ ]`.

---

## US-01 — Modelo base de persona

**Prioridad:** Alta · **Estado:** Implementada
**Como** desarrollador **quiero** una clase base `Person` **para** reutilizar los datos comunes de usuarios y administradores.

**Criterios de aceptación**

- [x] `Person` declara `id`, `name`, `lastName`, `email`, `phone`, `password`, `state` y `rol`.
- [x] Tiene constructor vacío, constructor por `id` y constructor completo.
- [x] Expone getters y setters para todos los campos.
- [x] `User` hereda de `Person` y agrega `city`.
- [x] `Admin` hereda de `Person` y agrega `area`.
- [x] La clase no importa `java.sql` ni clases de consola: es dominio puro.
- [ ] Los métodos vacíos `create/selectAll/selectById/update/deleteById/selectByRol` deben eliminarse o implementarse. *(brecha)*

---

## US-02 — Registro de usuario

**Prioridad:** Alta · **Estado:** Implementada
**Como** visitante **quiero** registrarme con mis datos **para** poder iniciar sesión y comprar.

**Criterios de aceptación**

- [x] Solicita nombre, apellido, correo, teléfono, contraseña y ciudad.
- [x] Todos los campos son obligatorios; el validador repite la pregunta si están vacíos.
- [x] El sistema asigna `rol = "User"` y `state = "Activo"`; no acepta otro rol desde la vista.
- [x] El ID lo genera MySQL con `AUTO_INCREMENT`; cualquier ID enviado se ignora.
- [x] El teléfono se guarda en `person.phone`.
- [x] Si el correo ya existe, se rechaza con "Ya existe una persona con ese correo electrónico." y no se inserta.
- [x] Al finalizar se muestra el ID generado.
- [ ] Validar formato de correo y teléfono antes de insertar. *(brecha)*
- [ ] No almacenar la contraseña en texto plano. *(brecha de seguridad)*

---

## US-03 — Inicio de sesión

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** usuario registrado **quiero** iniciar sesión **para** acceder a las funciones de mi rol.

**Criterios de aceptación**

- [x] Un usuario activo con credenciales correctas inicia sesión y conserva el ID real de `person`.
- [x] Se rechazan contraseña incorrecta, correo inexistente y cuenta inactiva.
- [x] El login de usuario solo acepta `rol = "User"`.
- [x] El correo se pasa como parámetro; `' OR '1'='1` no autentica.
- [x] La consulta filtra por correo, contraseña y estado con `PreparedStatement`.
- [x] Un administrador activo entra al menú de administrador.
- [ ] Unificar el valor del rol (`Admin` vs `ADMIN`) entre `dbData.sql` y `AdminRepositoryPortAdapterDB`. *(brecha: el admin del seed no inicia sesión)*
- [ ] El login de admin debe consultar por credenciales, no listar todos y comparar en memoria. *(brecha)*

---

## US-04 — Gestión de usuarios (administrador)

**Prioridad:** Alta · **Estado:** Implementada
**Como** administrador **quiero** gestionar usuarios **para** mantener actualizada la base de clientes.

**Criterios de aceptación**

- [x] Puede listar, consultar por ID, crear, actualizar y eliminar usuarios.
- [x] Solo opera sobre `rol = "User"`.
- [x] Al actualizar se conserva el estado actual si no se envía uno nuevo.
- [x] No se puede eliminar un usuario con órdenes asociadas; se informa el motivo.
- [x] El correo sigue siendo único.
- [x] Este módulo no puede modificar ni eliminar administradores.
- [ ] Cualquier error de base de datos debe mostrarse sin cerrar la sesión. *(brecha en algunos menús)*

---

## US-05 — Gestión de administradores

**Prioridad:** Alta · **Estado:** Implementada
**Como** administrador **quiero** gestionar administradores **para** controlar quién puede operar el sistema.

**Criterios de aceptación**

- [x] Crear administrador con nombre, apellido, correo, teléfono, contraseña, estado y área.
- [x] El rol se fuerza a `ADMIN`.
- [x] Listar solo administradores.
- [x] Consultar por ID solo si es administrador.
- [x] Actualizar y eliminar solo administradores.
- [x] Un administrador inactivo no puede iniciar sesión.
- [x] No se puede modificar ni eliminar una persona con rol `User` desde este módulo.
- [ ] No mostrar la contraseña en el listado. *(brecha)*

---

## US-06 — Gestión de productos

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** administrador **quiero** gestionar productos **para** mantener actualizado el catálogo de la cafetería y la papelería.

**Criterios de aceptación**

- [x] Crear producto con nombre, precio y tipo; el ID lo genera MySQL.
- [x] Listar todos los productos.
- [x] Consultar un producto por ID.
- [x] Actualizar nombre, precio y tipo.
- [x] Eliminar un producto.
- [x] Eliminar un producto referenciado por `cart_item` se bloquea por llave foránea y se informa.
- [ ] Validar que el precio sea numérico y mayor que cero. *(brecha)*
- [ ] `save` y `updateProduct` deben devolver el producto persistido, no `null`. *(brecha)*
- [ ] Usar `DECIMAL(12,2)`/`BigDecimal` en lugar de `FLOAT`/`Float`. *(brecha)*

---

## US-07 — Gestión de sedes

**Prioridad:** Alta · **Estado:** Implementada
**Como** administrador **quiero** gestionar sedes **para** definir puntos de venta y de entrega.

**Criterios de aceptación**

- [x] Crear sede con nombre y tipo; ambos obligatorios, recortados y de 1 a 100 caracteres.
- [x] Listar todas las sedes.
- [x] Consultar una sede por ID.
- [x] Actualizar nombre y tipo.
- [x] Actualizar solo el nombre conserva el tipo actual.
- [x] Eliminar una sede.
- [x] No se puede eliminar una sede con órdenes asociadas.
- [x] IDs inválidos (≤ 0) se rechazan.
- [x] El ID lo genera MySQL.

---

## US-08 — Carrito e ítems

**Prioridad:** Alta · **Estado:** Implementada
**Como** usuario **quiero** agregar productos a un carrito **para** preparar mi orden antes de pagar.

**Criterios de aceptación**

- [x] Crear un carrito con total 0.
- [x] Agregar un ítem con producto y cantidad ≥ 1.
- [x] El subtotal del ítem es precio unitario × cantidad.
- [x] El total del carrito se recalcula al agregar, quitar o actualizar ítems.
- [x] Rechazar producto nulo o cantidad < 1.
- [x] Quitar un ítem por ID y recalcular el total.
- [x] Eliminar el carrito elimina sus ítems (`ON DELETE CASCADE`).
- [x] Persistir en `shopping_cart` y `cart_item`.
- [ ] Actualizar el carrito (borrar y reinsertar ítems) debe ser transaccional. *(brecha)*

---

## US-09 — Crear orden (usuario)

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** usuario autenticado **quiero** crear una orden **para** comprar varios productos en una sola operación.

**Criterios de aceptación**

- [x] El usuario autenticado selecciona una sede existente y uno o más productos del catálogo.
- [x] Cada producto exige cantidad ≥ 1.
- [x] La orden debe tener al menos un producto.
- [x] El sistema crea el carrito, inserta los ítems y guarda la orden con el usuario de la sesión, la sede y el carrito.
- [x] El total se calcula desde los ítems, no se recibe del usuario.
- [x] El ID de la orden lo genera MySQL.
- [x] La orden aparece en "Ver mis órdenes".
- [ ] Un ID de producto inexistente debe mostrar "No existe ese producto" y continuar, no lanzar `NoSuchElementException`. *(brecha)*
- [ ] La creación de carrito + ítems + orden debe ser atómica. *(brecha)*

---

## US-10 — Consultar, actualizar y eliminar órdenes

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** usuario o administrador **quiero** gestionar órdenes **para** consultar el historial y corregir o anular compras.

**Criterios de aceptación**

- [x] El administrador lista todas las órdenes y consulta por ID.
- [x] El usuario solo ve sus propias órdenes.
- [x] El administrador puede actualizar sede, usuario y productos de una orden.
- [x] El usuario solo puede eliminar sus propias órdenes; el administrador puede eliminar cualquiera.
- [x] No se puede eliminar una orden con pago asociado; se informa y la sesión continúa.
- [x] El detalle muestra sede, usuario, productos, cantidades, precios y totales.
- [ ] Manejar órdenes con sede o usuario nulos sin `NullPointerException`. *(brecha)*
- [ ] La eliminación de orden y carrito debe ser transaccional y sin borrados duplicados. *(brecha)*

---

## US-11 — Registrar pago

**Prioridad:** Alta · **Estado:** Implementada
**Como** usuario **quiero** pagar una orden **para** confirmar mi compra y obtener un recibo.

**Criterios de aceptación**

- [x] Solo se paga una orden existente.
- [x] El usuario solo puede pagar sus propias órdenes; el administrador puede registrar el pago de cualquier orden.
- [x] Se rechaza una orden que ya tiene pago.
- [x] El método de pago debe existir en el catálogo.
- [x] El tipo de pago es obligatorio ("Pago Presencial" o "Pago Digital").
- [x] El total debe ser mayor que cero y se recalcula desde los ítems.
- [x] El usuario y el monto se derivan de la orden; no se confía en valores externos.
- [x] Se persiste `payment_type`, `order_id` y `method_id`; el ID lo genera MySQL.
- [x] `UNIQUE(order_id)` refuerza la regla de un pago por orden.
- [x] Se puede consultar, actualizar y eliminar un pago.
- [ ] Registrar el pago y actualizar el estado de la orden debe ser transaccional. *(brecha)*

---

## US-12 — Generar recibo

**Prioridad:** Alta · **Estado:** Implementada
**Como** usuario **quiero** consultar el recibo de mi pago **para** tener soporte de la compra.

**Criterios de aceptación**

- [x] Se genera por ID de pago.
- [x] Muestra ID de pago, tipo, método, ID de orden, ID de usuario, sede, productos, cantidad, precio unitario, subtotal y total.
- [x] Usa el precio histórico de `cart_item.price_by_quantity`, no el precio actual del producto.
- [x] El dueño puede consultar su recibo; otro usuario no.
- [x] Un ID inexistente muestra "No existe un pago con ese ID."
- [x] No inventa fecha ni estado que no existen en el esquema.
- [x] Las consultas cierran `Statement` y `ResultSet`.

---

## US-13 — Catálogo de métodos de pago

**Prioridad:** Media · **Estado:** Implementada
**Como** administrador **quiero** gestionar los métodos de pago **para** ofrecer las opciones disponibles en caja.

**Criterios de aceptación**

- [x] Crear, listar, consultar, actualizar y eliminar métodos.
- [x] Nombre obligatorio, recortado, de 1 a 100 caracteres.
- [x] ID generado por MySQL.
- [x] No se puede eliminar un método usado en un pago.
- [x] Al pagar se muestra el catálogo y se elige por ID.
- [x] Un ID inexistente se rechaza.
- [ ] Evitar nombres duplicados con una restricción `UNIQUE`. *(brecha)*

---

## US-14 — Menú por rol

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** usuario **quiero** un menú adaptado a mi rol **para** acceder solo a las funciones que me corresponden.

**Criterios de aceptación**

- [x] Menú principal: crear usuario, crear admin, iniciar sesión y salir.
- [x] El login enruta al menú de administrador o de usuario según el rol.
- [x] Menú admin: usuarios, admins, sedes, productos, órdenes, pagos, métodos de pago y cerrar sesión.
- [x] Menú usuario: ver productos, crear orden, ver mis órdenes, pagar, ver recibo, modificar datos, eliminar orden y cerrar sesión.
- [x] Una opción inválida muestra mensaje y repite el menú.
- [x] `0` cierra sesión o sale del programa.
- [x] El validador de enteros repite la pregunta ante una entrada no numérica.
- [ ] Todos los menús deben capturar errores y volver al menú, no terminar la sesión. *(brecha)*
- [ ] El validador no debe concatenar `null` en los mensajes de error. *(brecha)*

---

## US-15 — Persistencia MySQL (técnica)

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** desarrollador **quiero** persistir el sistema en MySQL **para** que los datos sobrevivan entre ejecuciones.

**Criterios de aceptación**

- [x] La conexión se configura con `NOTEBOOK_DB_URL`, `NOTEBOOK_DB_USER` y `NOTEBOOK_DB_PASSWORD`.
- [x] Si falta una variable, se lanza un error claro y no se intenta conectar.
- [x] `dbCreation.sql` crea las 8 tablas con PK, FK y `UNIQUE`.
- [x] `dbData.sql` carga usuarios, admins, sedes, productos, carritos, ítems, órdenes, métodos y pagos.
- [x] Todas las consultas usan `PreparedStatement`.
- [x] Los INSERT usan `RETURN_GENERATED_KEYS`.
- [x] La mayoría de recursos JDBC se cierran con `try-with-resources`.
- [x] No hay credenciales en el repositorio; solo `.env.example`.
- [ ] Usar transacciones (`setAutoCommit(false)`, `commit`, `rollback`) en operaciones de varios pasos. *(brecha)*
- [ ] Cerrar recursos también en `AdminRepositoryPortAdapterDB`. *(brecha)*
- [ ] Hashear contraseñas. *(brecha)*
- [ ] Migrar precios a `DECIMAL`. *(brecha)*

---

## US-16 — Verificaciones automáticas (técnica)

**Prioridad:** Alta · **Estado:** Implementada con brecha
**Como** desarrollador **quiero** verificaciones automáticas **para** demostrar que las reglas funcionan sin depender de MySQL.

**Criterios de aceptación**

- [x] Existen 6 clases: `PlaceChecks`, `PaymentMethodChecks`, `PaymentChecks`, `UserIdentityChecks`, `AdminIntegrationChecks` y `CliChecks`.
- [x] Compilan con JDK 25 y corren sin MySQL usando proxies de JDBC.
- [x] Verifican identidad persistida, autenticación, rol, estado y claves generadas.
- [x] Verifican rechazo de inyección SQL.
- [x] Verifican cálculo de totales y propiedad de órdenes/recibos.
- [x] Verifican un pago por orden y protección del rol Admin.
- [x] Verifican cierre de `Statement` y `ResultSet`.
- [x] `scripts/check.sh` compila todo y ejecuta las 6 verificaciones.
- [x] Las 6 terminan con código 0.
- [ ] Integrarlas con JUnit 5 y `mvn test`. *(brecha)*
- [ ] Incluir un script equivalente para PowerShell. *(brecha)*

---

## Definition of Done

- [ ] Compila con `javac --release 25`.
- [ ] Las 6 verificaciones pasan.
- [ ] Los scripts SQL se ejecutan en orden.
- [ ] No hay credenciales ni contraseñas reales en el repositorio.
- [ ] README con pasos de instalación y demo.
- [ ] `user-stories.md` completo y ubicado en `docs/`.
- [ ] Sin bugs P0 conocidos.

---

## Backlog priorizado (brechas)

| # | Brecha | Prioridad | Historia |
|---|---|---|---|
| 1 | Unificar `Admin`/`ADMIN` entre seed y consultas | Alta | US-03, US-05 |
| 2 | Corregir `Optional` en `OrderView` (producto inexistente) | Alta | US-09 |
| 3 | Transacciones en carrito, orden y pago | Alta | US-08, US-09, US-10, US-11 |
| 4 | Hash de contraseñas (BCrypt/PBKDF2) | Alta | US-02, US-03 |
| 5 | `DECIMAL(12,2)` + `BigDecimal` para dinero | Media | US-06, US-11 |
| 6 | Cerrar recursos en `AdminRepositoryPortAdapterDB` | Media | US-15 |
| 7 | Devolver el objeto persistido en productos | Media | US-06 |
| 8 | Evitar doble borrado de orden/carrito | Media | US-10 |
| 9 | Manejo de errores uniforme en todos los menús | Media | US-14 |
| 10 | JUnit 5 + Maven Surefire | Media | US-16 |
| 11 | README, diagrama y guía de demo | Media | DoD |
| 12 | Limpiar código muerto y enums sin uso | Baja | US-01, US-14 |

---

## Trazabilidad

| Historia | Clases principales | Verificación |
|---|---|---|
| US-01 | `domain/Person`, `User`, `Admin` | — |
| US-02 | `UserView`, `UserServiceAdapter`, `UserRepositoryAdapterDB` | `UserIdentityChecks` |
| US-03 | `AdminView.login`, `UserView.login`, `UserRepositoryAdapterDB.authenticate` | `UserIdentityChecks`, `AdminIntegrationChecks`, `CliChecks` |
| US-04 | `UserView`, `UserServiceAdapter`, `UserRepositoryAdapterDB` | `UserIdentityChecks` |
| US-05 | `AdminView`, `AdminServiceInterfaceAdapter`, `AdminRepositoryPortAdapterDB` | `AdminIntegrationChecks` |
| US-06 | `ProductView`, `ProductServiceAdapter`, `ProductRepositoryAdapterDB` | — |
| US-07 | `PlaceView`, `PlaceServiceAdapter`, `PlaceRepositoryAdapterDB` | `PlaceChecks` |
| US-08 | `Item`, `ShoppingCart`, `ItemServiceAdapter`, `ShoppingCartServiceAdapter` | `PaymentChecks` |
| US-09 | `OrderView`, `OrderServiceAdapter`, `OrderRepositoryAdapterDB` | `CliChecks` |
| US-10 | `OrderView`, `OrderServiceAdapter`, `OrderRepositoryAdapterDB` | `CliChecks` |
| US-11 | `PaymentView`, `PaymentServiceAdapter`, `PaymentRepositoryAdapterDB` | `PaymentChecks`, `CliChecks` |
| US-12 | `PaymentView.generateReceipt`, `PaymentRepositoryAdapterDB.findOrderById` | `PaymentChecks` |
| US-13 | `PaymentMethodView`, `PaymentMethodService`, `PaymentMethodRepository` | `PaymentMethodChecks`, `CliChecks` |
| US-14 | `CliUserInterface`, `DataTypeValidator` | `CliChecks` |
| US-15 | `DataBaseConnection`, `Config`, `database/*.sql` | `UserIdentityChecks` |
| US-16 | `src/test/java/app/*Checks.java`, `scripts/check.sh` | las 6 clases |
