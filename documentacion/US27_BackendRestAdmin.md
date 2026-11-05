# API HTTP del panel administrativo

`app.ApiApplication` expone la API usada por el frontend desde `http://localhost:8080/api`. Reutiliza los servicios y adaptadores de MySQL; el servidor solo escucha en `127.0.0.1`.

## Inicio

Configura `NOTEBOOK_DB_URL`, `NOTEBOOK_DB_USER` y `NOTEBOOK_DB_PASSWORD` en la ejecución de IntelliJ o en el entorno local. Aplica una vez `database/migrations/20261105_admin_email.sql` y `database/migrations/20261105_product_admin_fields.sql` a una base existente. Ejecuta la configuración local `Backend API` desde IntelliJ; esta conserva las variables privadas de la configuración actual y no se versiona. También puedes ejecutar `scripts/run-api.sh` si las variables están exportadas en la terminal. La variable opcional `NOTEBOOK_API_PORT` cambia el puerto y `NOTEBOOK_API_ORIGIN` define los orígenes web permitidos (por defecto `http://localhost:8780` y `http://localhost:63342`).

Rutas: `POST/DELETE /api/session`; `GET/POST/PUT/DELETE` para `/users`, `/admins`, `/places`, `/products` y `/payment-methods`; consultas `GET /orders` y `/payments`; `GET /health`.

Las credenciales se validan contra administradores activos en `person`. El backend devuelve una sesión aleatoria que vence a las ocho horas y nunca incluye contraseñas en las respuestas. El correo inicial del seed es `admin@admin.com`; la contraseña de ejemplo se encuentra en `database/dbData.sql`. Para bases ya pobladas se puede aplicar `database/migrations/20261105_admin_email.sql`, que solo corrige uno de los correos de ejemplo si el nuevo correo aún no existe.

El panel puede consultar órdenes y pagos persistidos; estos recursos son de solo lectura. El API no expone operaciones de checkout ni reemplaza el flujo visual académico del frontend.
