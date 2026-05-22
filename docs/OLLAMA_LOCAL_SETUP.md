# Configuración local de IA con Ollama

Este documento explica cómo configurar el proyecto para usar un servidor Ollama local (por ejemplo en http://localhost:11434) y cómo mantener un segundo proyecto/entorno en la nube sin la función de IA.

Requisitos previos
- Tener instalado Ollama localmente. En Windows puede instalarse desde https://ollama.com/docs/install
- Java y Maven para ejecutar la aplicación Spring Boot.

Pasos para ejecutar la IA local con Ollama

1) Iniciar Ollama local (ejemplo):

   - En Windows, tras instalar Ollama, ejecutar en PowerShell:

     ollama pull llama2
     ollama serve

   - Por defecto el servidor queda escuchando en http://localhost:11434

2) Configurar variables de entorno (PowerShell)

   - Puede usar el script incluido `load-env-ollama.ps1` (dot-source para conservar variables):

     . .\load-env-ollama.ps1

   - O configurar manualmente:

     setx AI_PROVIDER_URL "http://localhost:11434/api/generate"
     setx AI_PROVIDER_KEY ""

3) Ejecutar la aplicación Spring Boot (desde la raíz del proyecto):

   mvnw.cmd spring-boot:run

4) Probar la API localmente

   - Puede probar el endpoint de Ollama con curl:

     curl -X POST "http://localhost:11434/api/generate" -H "Content-Type: application/json" -d "{ \"model\": \"llama2\", \"prompt\": \"Hola\" }"

Notas sobre el proyecto en la nube (sin IA)

- Para desplegar la versión en nube sin IA, simplemente NO configure `AI_PROVIDER_URL` ni `AI_PROVIDER_KEY` en el entorno de despliegue del servicio. La aplicación cargará la URL por defecto (que apunta a un proveedor en la nube) — si desea asegurarse de que la funcionalidad IA no esté disponible, considere mantener una rama / build separada donde elimine o desactive los endpoints de IA.

- Alternativa práctica: usar variables de entorno distintas por entorno (por ejemplo, tener un `.env` local y otro `.env.cloud`) y cargar el apropiado durante despliegue.

Cambios realizados en el código

- El servicio IA ahora intenta reconocer respuestas en varios formatos (Cohere, Ollama, OpenAI/HF) para mayor compatibilidad con proveedores locales y en la nube.
- Para usar Ollama local solo es necesario apuntar `AI_PROVIDER_URL` a `http://localhost:11434/api/generate` y no enviar `AI_PROVIDER_KEY`.

