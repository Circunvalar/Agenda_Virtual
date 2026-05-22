# Agenda Virtual

Aplicación web construida con Spring Boot para gestionar agenda personal: eventos, tareas, recordatorios, contactos, calendario e integración con IA.

## Funcionalidades principales

- Registro e inicio de sesión de usuarios.
- Dashboard con resumen de eventos, tareas y recordatorios.
- Gestión de eventos con invitados.
- Gestión de recordatorios con estados y archivado lógico.
- Gestión de contactos por usuario.
- Vista de calendario con eventos y recordatorios.
- Asistente de IA para interpretar mensajes y crear recordatorios automáticamente.

## Tecnologías

- Java 17
- Spring Boot (Web, Security, Thymeleaf, Data JPA)
- PostgreSQL
- Maven Wrapper (`mvnw`)
- Docker / Docker Compose

## Estructura del proyecto

- `src/main/java/.../modulos/` lógica por dominios (`autentificacion`, `dashboard`, `eventos`, `recordatorios`, `contactos`, `calendario`, `tareas`, `ia`).
- `src/main/resources/templates/` vistas Thymeleaf.
- `src/main/resources/static/` recursos frontend (CSS y JS).
- `docker/postgres/init.sql` scripts de inicialización de base de datos.
- `compose.yaml` orquestación local de servicios.

## Variables de entorno relevantes

Configuradas en `application.properties` (con valores por defecto para desarrollo local):

- `APP_PORT` (por defecto: `4444`)
- `SPRING_DATASOURCE_URL` (por defecto: `jdbc:postgresql://localhost:5432/agenda_virtual`)
- `SPRING_DATASOURCE_USERNAME` (por defecto: `ADMINISTRADOR`)
- `SPRING_DATASOURCE_PASSWORD` (por defecto: `123456`)
- `AI_PROVIDER_URL` (por defecto: endpoint de Cohere)
- `AI_PROVIDER_KEY` (token del proveedor IA)

## Ejecución local

1. Asegura una instancia de PostgreSQL disponible.
2. Configura variables de entorno si no usarás los valores por defecto.
3. Ejecuta la aplicación:

```bash
sh mvnw spring-boot:run
```

Aplicación disponible en: `http://localhost:4444`

## Ejecución con Docker Compose

Levantar app + postgres (perfil `dev`):

```bash
docker compose --profile dev up --build
```

## Comandos útiles

Construir proyecto:

```bash
sh mvnw -DskipTests package
```

Ejecutar pruebas:

```bash
sh mvnw test
```

> Nota: las pruebas de contexto requieren PostgreSQL activo y accesible en la configuración del datasource.

## Rutas principales

- `/` página de inicio.
- `/login` y `/register` autenticación.
- `/dashboard` panel principal (requiere sesión).
- `/eventos`, `/recordatorios`, `/contactos`, `/calendario`, `/tareas` módulos de agenda.
- `/ia` vista del asistente.
- `/api/ia/**` endpoints REST de IA.
