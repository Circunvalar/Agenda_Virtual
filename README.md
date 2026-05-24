# Agenda Virtual IA

## Resumen
Agenda Virtual IA es una aplicacion web para gestionar eventos, recordatorios, tareas y contactos, con un asistente de IA integrado. La interfaz usa Thymeleaf y el backend esta construido con Spring Boot, JPA y PostgreSQL. La IA se integra via WebClient hacia un proveedor local (por defecto, Ollama).

## Arquitectura
- **Capa web (MVC):** controladores que renderizan vistas y exponen endpoints REST.
- **Capa de servicios:** logica de negocio por modulo (eventos, recordatorios, tareas, contactos, IA).
- **Capa de datos:** entidades JPA y repositorios para persistencia.
- **Seguridad:** Spring Security con login por formulario y roles.
- **IA:** servicio central que construye prompts, llama al proveedor y mantiene memoria de chat.

### Modulos principales (backend)
- **autentificacion:** registro y login de usuarios.
- **dashboard:** vista principal con resumen.
- **eventos:** CRUD de eventos e invitados.
- **recordatorios:** CRUD, repeticion y scheduler de notificaciones.
- **tareas:** CRUD basico de tareas.
- **contactos:** gestion de contactos y relacion con eventos.
- **calendario:** agregacion de eventos y recordatorios para calendario.
- **ia:** generacion de recordatorios, chat con memoria, priorizacion y operaciones por lenguaje natural.
- **usuarios:** entidad y repositorio de usuarios.
- **security:** configuracion de seguridad y UserDetails.

## IA: implementacion y uso
La integracion de IA se centraliza en `IAService` usando `WebClient`.

### Flujo general
1. El controlador recibe un mensaje del usuario.
2. `IAService` construye un prompt estricto (solo JSON).
3. Se realiza un `POST` al proveedor (por defecto `/api/generate` o `/api/chat`).
4. Se parsea la respuesta, se valida y se transforma a DTOs del sistema.
5. Para chat con memoria, se guarda el historial por usuario y se agrega al contexto.

### Endpoints de IA
- `POST /api/ia/recordatorio`: convierte un mensaje en recordatorio estructurado.
- `POST /api/ia/crear-recordatorios`: interpreta y guarda recordatorios automaticamente.
- `POST /api/ia/chat`: chat con memoria.
- `POST /api/ia/plan-diario`: genera plan diario.
- `POST /api/ia/plan-semanal`: genera plan semanal.
- `POST /api/ia/priorizar`: prioriza tareas existentes.
- `POST /api/ia/tareas-nl`: aplica acciones en tareas por lenguaje natural.
- `GET /api/ia/diagnostico`: prueba conectividad con el proveedor de IA.

### Memoria de chat
- El historial se guarda por usuario en `IAChatMensaje`.
- `IAService` agrega mensajes previos y el prompt de sistema antes de llamar al proveedor.
- El limite se controla con `ai.memory.limit`.

## Configuracion
Variables de entorno soportadas (ver `src/main/resources/application.properties`):
- `APP_PORT` (default: 4444)
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `AI_ENABLED` (default: true)
- `AI_PROVIDER_URL` (default: http://localhost:11434)
- `AI_PROVIDER_MODEL` (default: mistral)
- `AI_MEMORY_LIMIT` (default: 12)
- `AI_CONTEXT_CITY` (default: Bogota)
- `AI_CONTEXT_TIMEZONE` (default: America/Bogota)

## Ejecucion local (Windows PowerShell)
```powershell
cd C:\Agenda
. .\load-env.ps1
.\mvnw.cmd -DskipTests spring-boot:run
```

### Probar IA local
```powershell
Invoke-RestMethod -Uri "http://localhost:4444/api/ia/diagnostico" -Method Get
```

## Estructura del proyecto (backend)
```
src/main/java/com/circunvalar/edu/co/agendavirtual
├─ compartido
├─ modulos
│  ├─ autentificacion
│  ├─ calendario
│  ├─ contactos
│  ├─ dashboard
│  ├─ eventos
│  ├─ ia
│  ├─ recordatorios
│  ├─ tareas
│  └─ usuarios
└─ security
```

## Notas de mantenimiento
- Mantener prompts de IA con formato JSON estricto para parsing confiable.
- Evitar mutar listas de repositorios sin crear copias mutables.
- Reglas de acceso se encuentran en `ConfiguracionSeguridad`.
