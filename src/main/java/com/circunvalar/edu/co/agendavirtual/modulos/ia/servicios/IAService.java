package com.circunvalar.edu.co.agendavirtual.modulos.ia.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IARecordatorioDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.CategoriaRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.PrioridadRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.TipoRepeticion;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios.RecordatorioRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.net.UnknownHostException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.Duration;
import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAService {

    private final WebClient webClient;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final UsuarioRepositorio usuarioRepositorio;

    private final RecordatorioRepositorio recordatorioRepositorio;

    @Value("${ai.provider.url:api-inference.huggingface.co}")
    private String aiProviderUrl;

    public IAResponseDTO procesarMensaje(
            String mensajeUsuario
    ) {

        String prompt = construirPrompt(
                mensajeUsuario
        );

        // Llamar al proveedor de IA (Hugging Face)
        String respuesta = callHuggingFace(prompt);

        // La respuesta ya debe ser el texto generado por el modelo
        String contenido = respuesta;

        log.info("RESPUESTA IA: {}", contenido);

        return convertirRespuesta(contenido);

    }

    /*
        PROMPT
     */
    private String construirPrompt(
            String mensaje
    ) {

        return """
                Eres una IA para una agenda virtual.

                Debes convertir instrucciones
                en JSON válido.

                Categorías:
                - SALUD
                - TRABAJO
                - ESTUDIO
                - HOGAR
                - OTRO

                Prioridades:
                - BAJA
                - MEDIA
                - ALTA

                Tipos de repetición:
                - SIN_REPETICION
                - DIARIO
                - SEMANAL
                - MENSUAL
                - PERSONALIZADO

                Devuelve SOLO JSON.

                Ejemplo:

                {
                  "titulo":"Tomar agua",
                  "mensaje":"Beber agua",
                  "recordarAntesMinutos":30,
                  "repetitivo":true,
                  "tipoRepeticion":"DIARIO",
                  "intervaloDias":1,
                  "prioridad":"MEDIA",
                  "categoria":"SALUD",
                  "color":"#3b82f6"
                }

                Usuario:
                """ + mensaje;

    }

    /*
        PARSER JSON IA
     */
    private IAResponseDTO convertirRespuesta(
            String respuesta
    ) {

        try {

            String limpio = respuesta
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            JSONObject json =
                    new JSONObject(limpio);

            return IAResponseDTO.builder()

                    .titulo(
                            json.optString(
                                    "titulo",
                                    "Sin título"
                            )
                    )

                    .mensaje(
                            json.optString(
                                    "mensaje",
                                    ""
                            )
                    )

                    .fechaLimite(
                            json.optString(
                                    "fechaLimite",
                                    null
                            )
                    )

                    .recordarAntesMinutos(
                            json.optInt(
                                    "recordarAntesMinutos",
                                    30
                            )
                    )

                    .repetitivo(
                            json.optBoolean(
                                    "repetitivo",
                                    false
                            )
                    )

                    .intervaloDias(
                            json.optInt(
                                    "intervaloDias",
                                    0
                            )
                    )

                    .color(
                            json.optString(
                                    "color",
                                    "#6366f1"
                            )
                    )

                    .build();

        } catch (Exception e) {

            log.error(
                    "ERROR PARSEANDO IA",
                    e
            );

            return IAResponseDTO.builder()
                    .titulo("Error IA")
                    .mensaje(respuesta)
                    .build();

        }

    }

    /**
     * Helper que llama al endpoint de Hugging Face.
     * Envía {"inputs": prompt, "options": {"wait_for_model": true}}
     * y devuelve el texto generado por el modelo. Intenta extraer
     * "generated_text" si la respuesta viene en formato JSON.
     */
    private String callHuggingFace(String prompt){

        try{

            // Cohere-style payload
            Map<String,Object> payload = new HashMap<>();
            payload.put("model", "command-xsmall-nightly");
            payload.put("prompt", prompt);
            payload.put("max_tokens", 300);
            payload.put("temperature", 0.2);

            String response = webClient.post()
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if(response == null) return "";

            try{
                JsonNode node = objectMapper.readTree(response);

                // Cohere / some inference APIs: { generations: [ { text: "..." } ] }
                if(node.has("generations") && node.get("generations").isArray() && node.get("generations").size() > 0){
                    return node.get("generations").get(0).path("text").asText("");
                }

                // Ollama-like: { results: [ { content: [ { type: "output_text", text: "..." } ] } ] }
                if(node.has("results") && node.get("results").isArray() && node.get("results").size() > 0){
                    JsonNode firstResult = node.get("results").get(0);
                    if(firstResult.has("content") && firstResult.get("content").isArray() && firstResult.get("content").size() > 0){
                        for(JsonNode contentNode : firstResult.get("content")){
                            if(contentNode.has("text")){
                                String t = contentNode.path("text").asText("");
                                if(!t.isBlank()) return t;
                            }
                            if(contentNode.has("type") && contentNode.path("type").asText().equals("output_text") && contentNode.has("text")){
                                String t = contentNode.path("text").asText("");
                                if(!t.isBlank()) return t;
                            }
                        }
                    }
                    // Some versions may include a `text` at the result root
                    if(firstResult.has("text")){
                        String t = firstResult.path("text").asText("");
                        if(!t.isBlank()) return t;
                    }
                }

                // OpenAI-style / HF chat outputs: { choices: [ { message: { content: "..." } } ] } or choices[0].text
                if(node.has("choices") && node.get("choices").isArray() && node.get("choices").size() > 0){
                    JsonNode choice = node.get("choices").get(0);
                    if(choice.has("text")){
                        String t = choice.path("text").asText("");
                        if(!t.isBlank()) return t;
                    }
                    if(choice.has("message") && choice.get("message").has("content")){
                        String t = choice.get("message").path("content").asText("");
                        if(!t.isBlank()) return t;
                    }
                }

                // Fallback: devolver la representación cruda
                return response;

            }catch(Exception e){
                // No es JSON o no contiene campos esperados
                return response;
            }

        }catch(Exception e){

            // Buscar la causa raíz
            Throwable root = e;
            while(root.getCause() != null){
                root = root.getCause();
            }

            if(root instanceof UnknownHostException){
                String host = aiProviderUrl;
                String msg = "No se pudo resolver el host para el proveedor IA (" + host + "). \n" +
                        "Verifica que la máquina tenga conexión a Internet, que el DNS funcione, o si necesitas configurar un proxy/firewall.\n" +
                        "Mensaje original: " + root.getMessage();

                log.error(msg, e);

                throw new RuntimeException(msg, e);
            }

            log.error("ERROR llamando al proveedor IA", e);
            throw new RuntimeException("Error llamando al proveedor IA", e);
        }

    }
    public String organizarDia(
            String mensajeUsuario
    ) {

        String prompt = """
Eres una IA especializada en organizar tareas.

Debes convertir el texto del usuario en un JSON válido.

REGLAS IMPORTANTES:

1. SOLO responde JSON.
2. NO expliques nada.
3. NO uses markdown.
4. NO uses ```json
5. Siempre devuelve un ARRAY.
6. Todas las fechas deben tener formato:
yyyy-MM-ddTHH:mm:ss

7. Si el usuario NO especifica fecha:
- usar la fecha actual
- usar una hora lógica cercana

8. Categorías permitidas:
ESTUDIO
TRABAJO
SALUD
PERSONAL
OTRO

9. Prioridades permitidas:
ALTA
MEDIA
BAJA

Formato exacto:

[
  {
    "titulo": "string",
    "mensaje": "string",
    "fechaLimite": "2026-05-20T20:00:00",
    "prioridad": "ALTA",
    "categoria": "ESTUDIO"
  }
]

Texto del usuario:
""" + mensajeUsuario;
        JSONObject body = new JSONObject();

        body.put(
                "model",
                "mistral"
        );

        body.put(
                "prompt",
                prompt
        );

        body.put(
                "stream",
                false
        );

        // Llamamos al proveedor con el prompt ya construido
        String respuesta = callHuggingFace(prompt);

        return respuesta;
    }
    public String analizarTareas(String mensajeUsuario) {

        String prompt = """
            Eres una IA organizadora profesional.

            Analiza el siguiente mensaje y responde
            ÚNICAMENTE en formato JSON.

            Extrae:
            - titulo
            - descripcion
            - prioridad
            - categoria
            - duracionMinutos
            - fechaSugerida

            PRIORIDADES:
            ALTA
            MEDIA
            BAJA

            CATEGORIAS:
            ESTUDIO
            TRABAJO
            PERSONAL
            SALUD
            OTRO

            MENSAJE:
            """ + mensajeUsuario;

        Map<String, Object> body = new HashMap<>();

        body.put("model", "mistral");

        body.put("prompt", prompt);

        body.put("stream", false);

        // Llamada al proveedor de IA
        String respuesta = callHuggingFace(prompt);

        return respuesta;
    }
    public List<IARecordatorioDTO> interpretarTareas(
            String mensaje
    ) {

        LocalDateTime ahora =
                LocalDateTime.now();

        String prompt = """
Eres una IA experta en organización de tareas.

Tu única función es convertir mensajes
en tareas JSON válidas para una agenda virtual.

REGLAS OBLIGATORIAS:

1. RESPONDE SOLO JSON
2. NO markdown
3. NO ```json
4. NO explicaciones
5. NO texto extra
6. DEVUELVE SIEMPRE UN ARRAY JSON
7. NUNCA dejes campos vacíos
8. NUNCA uses null
9. NUNCA uses ""

CAMPOS OBLIGATORIOS:

- titulo
- mensaje
- fechaLimite
- prioridad
- categoria

FORMATO FECHA:

yyyy-MM-ddTHH:mm:ss

REGLAS FECHA:

- Si el usuario dice "mañana":
  usar el día siguiente

- Si dice "hoy":
  usar hoy

- Si dice:
  noche -> 20:00
  tarde -> 15:00
  mañana temprano -> 08:00

- Si NO especifica fecha:
  usar una fecha cercana válida

- NUNCA dejar fecha vacía

- NUNCA inventar años lejanos

- TODAS las fechas deben estar
  entre hoy y los próximos 30 días

PRIORIDADES VÁLIDAS:

ALTA
MEDIA
BAJA

Si no se especifica:
usar MEDIA

CATEGORÍAS VÁLIDAS:

TRABAJO
ESTUDIO
SALUD
PERSONAL
OTRO

Si no se identifica:
usar OTRO

REGLAS TITULO:

- corto
- máximo 4 palabras
- nunca vacío

REGLAS MENSAJE:

- descriptivo
- claro
- nunca vacío

EJEMPLO CORRECTO:

[
  {
    "titulo":"Estudiar cálculo",
    "mensaje":"Repasar temas para examen",
    "fechaLimite":"2026-05-22T19:00:00",
    "prioridad":"ALTA",
    "categoria":"ESTUDIO"
  }
]

FECHA ACTUAL:
""" + ahora + """

MENSAJE USUARIO:
""" + mensaje;

        Map<String, Object> request = new HashMap<>();

        request.put("model", "mistral");

        request.put("prompt", prompt);

        request.put("stream", false);

        request.put("temperature", 0.1);

        // Llamada al proveedor de IA
        String respuestaIA = callHuggingFace(prompt);

        log.info(
                "RESPUESTA IA INTERPRETAR: {}",
                respuestaIA
        );

        try {

            String limpio = respuestaIA
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return objectMapper.readValue(
                    limpio,
                    new TypeReference<
                            List<IARecordatorioDTO>>() {}
            );

        } catch (Exception e) {

            log.error(
                    "ERROR PARSEANDO IA",
                    e
            );

            throw new RuntimeException(
                    "Error interpretando JSON IA: "
                            + respuestaIA
            );

        }

    }
    public List<RecordatorioRequestDTO> convertirARecordatorios(
            List<IARecordatorioDTO> tareasIA
    ) {

        return tareasIA.stream()
                .map(tarea -> {

                    RecordatorioRequestDTO dto =
                            new RecordatorioRequestDTO();

                /*
                    TITULO
                 */
                    dto.setTitulo(
                            valorSeguro(
                                    tarea.getTitulo(),
                                    "Recordatorio"
                            )
                    );

                /*
                    MENSAJE
                 */
                    dto.setMensaje(
                            valorSeguro(
                                    tarea.getMensaje(),
                                    "Actividad generada por IA"
                            )
                    );

                /*
                    FECHA
                 */
                    LocalDateTime fechaFinal;

                    try {

                        String fechaTexto =
                                valorSeguro(
                                        tarea.getFechaLimite(),
                                        ""
                                );

                        if(fechaTexto.isBlank()){

                            fechaFinal =
                                    LocalDateTime.now()
                                            .plusHours(2);

                        }else{

                            fechaFinal =
                                    LocalDateTime.parse(
                                            fechaTexto
                                    );

                        }

                    } catch (Exception e) {

                        fechaFinal =
                                LocalDateTime.now()
                                        .plusHours(2);

                    }

                /*
                    VALIDAR FECHA
                 */
                    if(
                            fechaFinal.isBefore(
                                    LocalDateTime.now()
                            )
                    ){

                        fechaFinal =
                                LocalDateTime.now()
                                        .plusHours(2);

                    }

                    if(
                            fechaFinal.isAfter(
                                    LocalDateTime.now()
                                            .plusDays(30)
                            )
                    ){

                        fechaFinal =
                                LocalDateTime.now()
                                        .plusDays(1);

                    }

                    dto.setFechaLimite(
                            fechaFinal
                    );

                /*
                    PRIORIDAD
                 */
                    try {

                        dto.setPrioridad(
                                PrioridadRecordatorio.valueOf(
                                        valorSeguro(
                                                tarea.getPrioridad(),
                                                "MEDIA"
                                        ).toUpperCase()
                                )
                        );

                    } catch (Exception e) {

                        dto.setPrioridad(
                                PrioridadRecordatorio.MEDIA
                        );

                    }

                /*
                    CATEGORIA
                 */
                    try {

                        dto.setCategoria(
                                CategoriaRecordatorio.valueOf(
                                        valorSeguro(
                                                tarea.getCategoria(),
                                                "OTRO"
                                        ).toUpperCase()
                                )
                        );

                    } catch (Exception e) {

                        dto.setCategoria(
                                CategoriaRecordatorio.OTRO
                        );

                    }

                    dto.setColor("#6366f1");

                    dto.setRepetitivo(false);

                    dto.setRecordarAntesMinutos(30);

                    dto.setIntervaloDias(0);

                    return dto;

                })
                .toList();
    }

    public void guardarRecordatoriosIA(
            List<RecordatorioRequestDTO> tareas,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Recordatorio> nuevos = tareas.stream()

                .filter(dto ->
                        dto.getTitulo() != null
                                &&
                                !dto.getTitulo().isBlank()
                )

                .map(dto -> {

                    LocalDateTime fechaFinal =
                            dto.getFechaLimite();

                    if(fechaFinal == null){

                        fechaFinal =
                                LocalDateTime.now()
                                        .plusHours(2);

                    }

                    return Recordatorio.builder()

                            .titulo(dto.getTitulo())

                            .mensaje(dto.getMensaje())

                            .fechaLimite(fechaFinal)

                            .prioridad(dto.getPrioridad())

                            .categoria(dto.getCategoria())

                            .color(dto.getColor())

                            .completado(false)

                            .archivado(false)

                            .notificado(false)

                            .repetitivo(false)

                            .tipoRepeticion(
                                    TipoRepeticion.SIN_REPETICION
                            )

                            .recordarAntesMinutos(30)

                            .intervaloDias(0)

                            .creador(usuario)

                            .build();

                })

                .toList();

        recordatorioRepositorio.saveAll(nuevos);

    }
    private String valorSeguro(
            String valor,
            String defecto
    ) {

        if(valor == null){

            return defecto;

        }

        String limpio =
                valor.trim();

        if(
                limpio.isBlank()
                        ||
                        limpio.equalsIgnoreCase("null")
                        ||
                        limpio.equalsIgnoreCase("undefined")
        ){

            return defecto;

        }

        return limpio;

    }
    private LocalDateTime fechaPorDefecto() {

        return LocalDateTime.now()
                .plusHours(2);

    }

    /**
     * Diagnóstico rápido para comprobar resolución DNS y conectividad HTTP
     * hacia el proveedor configurado en `ai.provider.url`.
     * Devuelve un Map con información de host, ips, y estado HTTP si es posible.
     */
    public Map<String,Object> diagnosticarProveedor(){

        Map<String,Object> resultado = new HashMap<>();

        try{
            String raw = aiProviderUrl == null ? "" : aiProviderUrl;
            String urlStr = raw;
            if(!urlStr.startsWith("http")){
                urlStr = "https://" + urlStr;
            }

            URI uri = new URI(urlStr);
            String host = uri.getHost();

            resultado.put("configuredUrl", urlStr);
            resultado.put("host", host);

            InetAddress[] addrs = InetAddress.getAllByName(host);
            List<String> ips = Arrays.stream(addrs)
                    .map(InetAddress::getHostAddress)
                    .collect(Collectors.toList());

            resultado.put("resolvedIps", ips);

        }catch(Exception e){
            resultado.put("dnsError", e.toString());
        }

        // Intentar una llamada HTTP ligera al baseUrl para ver si responde
        try{
            // Usamos GET vacío para obtener el status (puede devolver 401/403 si falta auth)
            Integer status = webClient.get()
                    .uri("")
                    .exchangeToMono(resp -> reactor.core.publisher.Mono.just(resp.statusCode().value()))
                    .block(Duration.ofSeconds(10));

            resultado.put("httpStatus", status);

        }catch(Exception e){
            resultado.put("httpError", e.toString());
        }

        return resultado;

    }
}