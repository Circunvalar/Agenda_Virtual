package com.circunvalar.edu.co.agendavirtual.modulos.ia.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IARecordatorioDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAChatRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAChatResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IATareaAccionDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IATareaOperacionResultadoDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.entidades.IAChatMensaje;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.entidades.IAChatRol;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.repositorios.IAChatMensajeRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.CategoriaRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.PrioridadRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.TipoRepeticion;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios.RecordatorioRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos.TareaResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades.Prioridad;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades.Tarea;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.repositorios.TareaRepositorio;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Servicio central de IA: prompts, parsing, chat con memoria y tareas por lenguaje natural.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IAService {

    private final WebClient webClient;

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final UsuarioRepositorio usuarioRepositorio;

    private final RecordatorioRepositorio recordatorioRepositorio;

    private final TareaRepositorio tareaRepositorio;

    private final IAChatMensajeRepositorio iaChatMensajeRepositorio;

    @Value("${ai.provider.url:http://localhost:11434}")
    private String aiBaseUrl;

    @Value("${ai.provider.model:mistral}")
    private String aiModel;

    @Value("${ai.memory.limit:12}")
    private int memoryLimit;

    @Value("${ai.context.city:Bogota}")
    private String contextCity;

    @Value("${ai.context.timezone:America/Bogota}")
    private String contextTimeZone;

    @Value("${ai.enabled:true}")
    private boolean aiEnabled;

    /**
     * Convierte un mensaje en un recordatorio estructurado usando IA.
     */
    public IAResponseDTO procesarMensaje(
            String mensajeUsuario
    ) {

        if(!aiEnabled){

            return IAResponseDTO.builder()
                    .titulo("IA deshabilitada")
                    .mensaje("La IA esta desactivada en este entorno")
                    .build();

        }

        String prompt = construirPrompt(
                mensajeUsuario
        );

        JSONObject body = new JSONObject();

        body.put(
                "model",
                aiModel
        );

        body.put(
                "prompt",
                prompt
        );

        body.put(
                "stream",
                false
        );

        String respuesta = webClient.post()
                .uri("/api/generate")
                .bodyValue(body.toString())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JSONObject jsonRespuesta =
                new JSONObject(respuesta);

        String contenido =
                jsonRespuesta.getString("response");

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
     * Genera una lista JSON de tareas para organizar el dia del usuario.
     */
    public String organizarDia(
            String mensajeUsuario
    ) {

        if(!aiEnabled){
            return "[]";
        }

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
                aiModel
        );

        body.put(
                "prompt",
                prompt
        );

        body.put(
                "stream",
                false
        );

        String respuesta =
                webClient.post()
                        .uri("/api/generate")
                        .bodyValue(body.toString())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

        JSONObject json =
                new JSONObject(respuesta);

        return json.getString(
                "response"
        );
    }

    /**
     * Analiza texto y devuelve un JSON con atributos de una tarea.
     */
    public String analizarTareas(String mensajeUsuario) {

        if(!aiEnabled){
            return "{}";
        }

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

        body.put("model", aiModel);

        body.put("prompt", prompt);

        body.put("stream", false);

        Map<String, Object> response = webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return Objects.toString(response.get("response"), "");
    }

    /**
     * Interpreta texto en una lista de recordatorios sugeridos.
     */
    public List<IARecordatorioDTO> interpretarTareas(
            String mensaje
    ) {

        if(!aiEnabled){
            return List.of();
        }

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

        request.put("model", aiModel);

        request.put("prompt", prompt);

        request.put("stream", false);

        request.put("temperature", 0.1);

        Map response = webClient.post()
                .uri("/api/generate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String respuestaIA = Objects.toString(
                response.get("response"),
                ""
        );

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

    /**
     * Convierte recordatorios sugeridos por IA en DTOs de entrada del sistema.
     */
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

    /**
     * Verifica conectividad con el proveedor de IA.
     */
    public Map<String, Object> diagnosticarOllama() {

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("baseUrl", aiBaseUrl);

        if(!aiEnabled){
            resultado.put("ok", false);
            resultado.put("error", "IA deshabilitada");
            return resultado;
        }

        try {

            Map<String, Object> response = webClient.get()
                    .uri("/api/tags")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(5));

            resultado.put("ok", true);
            resultado.put("response", response);

        } catch (Exception e) {

            resultado.put("ok", false);
            resultado.put("error", e.getMessage());

        }

        return resultado;

    }

    /**
     * Chat con memoria contextual del usuario.
     */
    public IAChatResponseDTO chatConMemoria(
            IAChatRequestDTO request,
            String username
    ) {

        if(!aiEnabled){

            return IAChatResponseDTO.builder()
                    .respuesta("IA deshabilitada")
                    .build();

        }

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Map<String, String>> mensajes =
                construirMensajesConMemoria(
                        usuario,
                        "CHAT",
                        request.getPreferencias(),
                        request.getMensaje()
                );

        String respuesta = llamarOllamaChat(mensajes);

        guardarMensaje(IAChatRol.USER, request.getMensaje(), usuario);
        guardarMensaje(IAChatRol.ASSISTANT, respuesta, usuario);

        return IAChatResponseDTO.builder()
                .respuesta(respuesta)
                .build();

    }

    /**
     * Genera un plan diario basado en contexto y preferencias.
     */
    public IAChatResponseDTO generarPlanDiario(
            IAChatRequestDTO request,
            String username
    ) {

        return generarPlan(
                request,
                username,
                "PLAN_DIARIO"
        );

    }

    /**
     * Genera un plan semanal basado en contexto y preferencias.
     */
    public IAChatResponseDTO generarPlanSemanal(
            IAChatRequestDTO request,
            String username
    ) {

        return generarPlan(
                request,
                username,
                "PLAN_SEMANAL"
        );

    }

    /**
     * Prioriza tareas existentes segun contexto del usuario.
     */
    public IAChatResponseDTO priorizarTareas(
            IAChatRequestDTO request,
            String username
    ) {

        if(!aiEnabled){

            return IAChatResponseDTO.builder()
                    .respuesta("IA deshabilitada")
                    .build();

        }

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Tarea> tareas =
                tareaRepositorio.findByCreador(usuario);

        String contextoTareas =
                construirContextoTareas(tareas);

        String contenidoUsuario = "Priorizacion de tareas.\n" +
                "Instruccion del usuario: " +
                valorSeguro(request.getMensaje(), "") +
                "\nContexto de tareas:\n" +
                contextoTareas +
                "\nPreferencias: " +
                valorSeguro(request.getPreferencias(), "");

        List<Map<String, String>> mensajes =
                construirMensajesConMemoria(
                        usuario,
                        "PRIORIZAR",
                        request.getPreferencias(),
                        contenidoUsuario
                );

        String respuesta = llamarOllamaChat(mensajes);

        guardarMensaje(IAChatRol.USER, request.getMensaje(), usuario);
        guardarMensaje(IAChatRol.ASSISTANT, respuesta, usuario);

        return IAChatResponseDTO.builder()
                .respuesta(respuesta)
                .build();

    }

    /**
     * Ejecuta operaciones sobre tareas a partir de lenguaje natural.
     */
    public IATareaOperacionResultadoDTO procesarTareasNL(
            IAChatRequestDTO request,
            String username
    ) {

        if(!aiEnabled){

            return IATareaOperacionResultadoDTO.builder()
                    .advertencias(
                            List.of("IA deshabilitada")
                    )
                    .build();

        }

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Tarea> tareas =
                tareaRepositorio.findByCreador(usuario);

        String contextoTareas =
                construirContextoTareas(tareas);

        String contenidoUsuario = "Instruccion: " +
                request.getMensaje() +
                "\n\nTareas actuales:\n" +
                contextoTareas +
                "\n\nDevuelve SOLO JSON como ARRAY de acciones." +
                " Cada objeto debe tener: accion, id (opcional), " +
                "titulo, descripcion, prioridad, fechaLimite." +
                " Acciones validas: CREAR, ACTUALIZAR, COMPLETAR, ELIMINAR." +
                " fechaLimite en formato yyyy-MM-dd.";

        List<Map<String, String>> mensajes =
                construirMensajesConMemoria(
                        usuario,
                        "TAREAS_NL",
                        request.getPreferencias(),
                        contenidoUsuario
                );

        String respuesta = llamarOllamaChat(mensajes);

        List<IATareaAccionDTO> acciones;

        try {

            String limpio = respuesta
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            acciones = objectMapper.readValue(
                    limpio,
                    new TypeReference<List<IATareaAccionDTO>>() {}
            );

        } catch (Exception e) {

            return IATareaOperacionResultadoDTO.builder()
                    .advertencias(
                            List.of(
                                    "No se pudo interpretar JSON de acciones"
                            )
                    )
                    .build();

        }

        IATareaOperacionResultadoDTO resultado =
                aplicarAccionesTareas(acciones, usuario);

        guardarMensaje(IAChatRol.USER, request.getMensaje(), usuario);
        guardarMensaje(IAChatRol.ASSISTANT, respuesta, usuario);

        return resultado;

    }

    private IAChatResponseDTO generarPlan(
            IAChatRequestDTO request,
            String username,
            String modo
    ) {

        if(!aiEnabled){

            return IAChatResponseDTO.builder()
                    .respuesta("IA deshabilitada")
                    .build();

        }

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Tarea> tareas =
                tareaRepositorio.findByCreador(usuario);

        String contextoTareas =
                construirContextoTareas(tareas);

        String contenidoUsuario = "Genera un plan " +
                ("PLAN_DIARIO".equals(modo) ? "diario" : "semanal") +
                " optimizado.\n" +
                "Instruccion del usuario: " +
                valorSeguro(request.getMensaje(), "") +
                "\nTareas actuales:\n" +
                contextoTareas +
                "\nPreferencias: " +
                valorSeguro(request.getPreferencias(), "");

        List<Map<String, String>> mensajes =
                construirMensajesConMemoria(
                        usuario,
                        modo,
                        request.getPreferencias(),
                        contenidoUsuario
                );

        String respuesta = llamarOllamaChat(mensajes);

        guardarMensaje(IAChatRol.USER, request.getMensaje(), usuario);
        guardarMensaje(IAChatRol.ASSISTANT, respuesta, usuario);

        return IAChatResponseDTO.builder()
                .respuesta(respuesta)
                .build();

    }

    private List<Map<String, String>> construirMensajesConMemoria(
            Usuario usuario,
            String modo,
            String preferencias,
            String contenidoUsuario
    ) {

        List<Map<String, String>> mensajes =
                new ArrayList<>();

        mensajes.add(
                Map.of(
                        "role",
                        "system",
                        "content",
                        construirPromptSistema(
                                modo,
                                preferencias,
                                usuario
                        )
                )
        );

        // Obtener historial (mutable) y añadirlo a la lista de mensajes
        List<IAChatMensaje> historial = obtenerHistorial(usuario);

        for (IAChatMensaje mensajeHist : historial) {

            mensajes.add(
                    Map.of(
                            "role",
                            mapearRol(mensajeHist.getRol()),
                            "content",
                            mensajeHist.getContenido()
                    )
            );

        }

        mensajes.add(
                Map.of(
                        "role",
                        "user",
                        "content",
                        contenidoUsuario
                )
        );

        return mensajes;

    }

    private String construirPromptSistema(
            String modo,
            String preferencias,
            Usuario usuario
    ) {

        ZonedDateTime ahora =
                ZonedDateTime.now(
                        ZoneId.of(contextTimeZone)
                );

        String fechaHora = ahora.format(
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm"
                )
        );

        return "Eres un asistente de agenda virtual. " +
                "Responde en espanol neutro y claro. " +
                "Contexto local: " + contextCity + ". " +
                "Considera trafico en horas pico (06:00-09:00 y 17:00-20:00) " +
                "y clima cambiante; sugiere margen de desplazamiento. " +
                "Fecha/hora local: " + fechaHora + ". " +
                "Modo: " + modo + ". " +
                "Preferencias del usuario: " +
                valorSeguro(preferencias, "") + ".";

    }

    private List<IAChatMensaje> obtenerHistorial(
            Usuario usuario
    ) {

        List<IAChatMensaje> mensajesOriginales = iaChatMensajeRepositorio
                .findByUsuarioOrderByCreatedAtDesc(
                        usuario,
                        PageRequest.of(0, memoryLimit)
                )
                .getContent();

        if(mensajesOriginales == null || mensajesOriginales.isEmpty()){
            return new ArrayList<>();
        }

        List<IAChatMensaje> mensajes = new ArrayList<>(mensajesOriginales);
        Collections.reverse(mensajes);

        return mensajes;

    }

    private void guardarMensaje(
            IAChatRol rol,
            String contenido,
            Usuario usuario
    ) {

        if(contenido == null || contenido.isBlank()){

            return;

        }

        IAChatMensaje mensaje = IAChatMensaje.builder()
                .rol(rol)
                .contenido(contenido)
                .usuario(usuario)
                .build();

        iaChatMensajeRepositorio.save(mensaje);

    }

    private String mapearRol(
            IAChatRol rol
    ) {

        return switch (rol) {
            case SYSTEM -> "system";
            case USER -> "user";
            case ASSISTANT -> "assistant";
        };

    }

    private String llamarOllamaChat(
            List<Map<String, String>> mensajes
    ) {

        Map<String, Object> body = new HashMap<>();

        body.put("model", aiModel);
        body.put("messages", mensajes);
        body.put("stream", false);

        Map<String, Object> response = webClient.post()
                .uri("/api/chat")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return extraerContenidoChat(response);

    }

    private String extraerContenidoChat(
            Map<String, Object> respuesta
    ) {

        if(respuesta == null){

            return "";

        }

        Object mensaje = respuesta.get("message");

        if(mensaje instanceof Map){

            Map<?, ?> mensajeMap = (Map<?, ?>) mensaje;
            Object contenido = mensajeMap.get("content");

            if(contenido != null){

                return contenido.toString();

            }

        }

        Object respuestaTexto = respuesta.get("response");

        if(respuestaTexto != null){

            return respuestaTexto.toString();

        }

        return respuesta.toString();

    }

    private String construirContextoTareas(
            List<Tarea> tareas
    ) {

        if(tareas == null || tareas.isEmpty()){

            return "Sin tareas registradas.";

        }

        StringBuilder builder = new StringBuilder();

        for (Tarea tarea : tareas) {

            builder.append("- [")
                    .append(tarea.getId())
                    .append("] ")
                    .append(valorSeguro(tarea.getTitulo(), "Sin titulo"))
                    .append(" | prioridad: ")
                    .append(tarea.getPrioridad())
                    .append(" | limite: ")
                    .append(tarea.getFechaLimite())
                    .append(" | completada: ")
                    .append(Boolean.TRUE.equals(tarea.getCompletada()))
                    .append("\n");

        }

        return builder.toString();

    }

    private IATareaOperacionResultadoDTO aplicarAccionesTareas(
            List<IATareaAccionDTO> acciones,
            Usuario usuario
    ) {

        IATareaOperacionResultadoDTO resultado =
                IATareaOperacionResultadoDTO.builder()
                        .build();

        for (IATareaAccionDTO accion : acciones) {

            if(accion == null){

                continue;

            }

            String tipo = valorSeguro(accion.getAccion(), "").toUpperCase();

            switch (tipo) {
                case "CREAR" -> crearTarea(accion, usuario, resultado);
                case "ACTUALIZAR" -> actualizarTarea(accion, usuario, resultado);
                case "COMPLETAR" -> completarTarea(accion, usuario, resultado);
                case "ELIMINAR" -> eliminarTarea(accion, usuario, resultado);
                default -> resultado.getAdvertencias()
                        .add("Accion desconocida: " + accion.getAccion());
            }

        }

        return resultado;

    }

    private void crearTarea(
            IATareaAccionDTO accion,
            Usuario usuario,
            IATareaOperacionResultadoDTO resultado
    ) {

        Tarea tarea = Tarea.builder()
                .titulo(
                        valorSeguro(
                                accion.getTitulo(),
                                "Tarea sin titulo"
                        )
                )
                .descripcion(accion.getDescripcion())
                .prioridad(parsearPrioridad(accion.getPrioridad()))
                .completada(false)
                .fechaLimite(parsearFecha(accion.getFechaLimite(), resultado))
                .creador(usuario)
                .build();

        tareaRepositorio.save(tarea);

        resultado.getCreadas().add(convertirTarea(tarea));

    }

    private void actualizarTarea(
            IATareaAccionDTO accion,
            Usuario usuario,
            IATareaOperacionResultadoDTO resultado
    ) {

        Tarea tarea = buscarTarea(usuario, accion.getId(), accion.getTitulo());

        if(tarea == null){

            resultado.getAdvertencias().add(
                    "No se encontro tarea para actualizar"
            );
            return;

        }

        if(accion.getTitulo() != null && !accion.getTitulo().isBlank()){

            tarea.setTitulo(accion.getTitulo());

        }

        if(accion.getDescripcion() != null){

            tarea.setDescripcion(accion.getDescripcion());

        }

        if(accion.getPrioridad() != null){

            tarea.setPrioridad(parsearPrioridad(accion.getPrioridad()));

        }

        if(accion.getFechaLimite() != null){

            tarea.setFechaLimite(parsearFecha(accion.getFechaLimite(), resultado));

        }

        tareaRepositorio.save(tarea);

        resultado.getActualizadas().add(convertirTarea(tarea));

    }

    private void completarTarea(
            IATareaAccionDTO accion,
            Usuario usuario,
            IATareaOperacionResultadoDTO resultado
    ) {

        Tarea tarea = buscarTarea(usuario, accion.getId(), accion.getTitulo());

        if(tarea == null){

            resultado.getAdvertencias().add(
                    "No se encontro tarea para completar"
            );
            return;

        }

        tarea.setCompletada(true);

        tareaRepositorio.save(tarea);

        resultado.getCompletadas().add(tarea.getId().toString());

    }

    private void eliminarTarea(
            IATareaAccionDTO accion,
            Usuario usuario,
            IATareaOperacionResultadoDTO resultado
    ) {

        Tarea tarea = buscarTarea(usuario, accion.getId(), accion.getTitulo());

        if(tarea == null){

            resultado.getAdvertencias().add(
                    "No se encontro tarea para eliminar"
            );
            return;

        }

        tareaRepositorio.delete(tarea);

        resultado.getEliminadas().add(tarea.getId().toString());

    }

    private Tarea buscarTarea(
            Usuario usuario,
            String id,
            String titulo
    ) {

        if(id != null && !id.isBlank()){

            try {

                UUID uuid = UUID.fromString(id.trim());

                return tareaRepositorio.findById(uuid)
                        .filter(t -> t.getCreador() != null &&
                                t.getCreador().getId().equals(usuario.getId()))
                        .orElse(null);

            } catch (Exception e) {

                return null;

            }

        }

        if(titulo != null && !titulo.isBlank()){

            List<Tarea> tareas = tareaRepositorio
                    .findByCreadorAndTituloIgnoreCase(
                            usuario,
                            titulo.trim()
                    );

            if(!tareas.isEmpty()){

                return tareas.get(0);

            }

        }

        return null;

    }

    private Prioridad parsearPrioridad(
            String prioridad
    ) {

        if(prioridad == null || prioridad.isBlank()){

            return Prioridad.MEDIA;

        }

        try {

            return Prioridad.valueOf(
                    prioridad.trim().toUpperCase()
            );

        } catch (Exception e) {

            return Prioridad.MEDIA;

        }

    }

    private LocalDate parsearFecha(
            String fecha,
            IATareaOperacionResultadoDTO resultado
    ) {

        if(fecha == null || fecha.isBlank()){

            return null;

        }

        try {

            return LocalDate.parse(fecha.trim());

        } catch (Exception e) {

            resultado.getAdvertencias().add(
                    "Fecha invalida: " + fecha
            );
            return null;

        }

    }

    private TareaResponseDTO convertirTarea(
            Tarea tarea
    ) {

        return TareaResponseDTO.builder()
                .id(tarea.getId())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .prioridad(
                        tarea.getPrioridad() != null
                                ? tarea.getPrioridad().name()
                                : null
                )
                .completada(tarea.getCompletada())
                .fechaLimite(tarea.getFechaLimite())
                .creador(
                        tarea.getCreador() != null
                                ? tarea.getCreador().getNombreDeUsuario()
                                : null
                )
                .invitados(
                        tarea.getInvitados() == null
                                ? List.of()
                                : tarea.getInvitados().stream()
                                .map(Usuario::getNombreDeUsuario)
                                .toList()
                )
                .build();

    }


}
