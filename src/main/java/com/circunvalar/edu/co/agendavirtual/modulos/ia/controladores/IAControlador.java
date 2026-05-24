package com.circunvalar.edu.co.agendavirtual.modulos.ia.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAChatRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAChatResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAMensajeDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IARecordatorioDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IARequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IAResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.IATareaOperacionResultadoDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.servicios.IAService;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Endpoints REST para funciones de IA y automatizacion.
 */
@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IAControlador {

    private final IAService iaService;

    /**
     * Convierte un mensaje en un recordatorio estructurado.
     */
    @PostMapping("/recordatorio")
    public ResponseEntity<IAResponseDTO> procesarRecordatorio(
            @Valid
            @RequestBody
            IARequestDTO dto
    ) {

        IAResponseDTO respuesta =
                iaService.procesarMensaje(
                        dto.getMensaje()
                );

        return ResponseEntity.ok(
                respuesta
        );
    }

    /**
     * Genera un resumen para organizar el dia.
     */
    @PostMapping("/organizar-dia")
    public ResponseEntity<IAChatResponseDTO> organizarDia(

            @RequestBody
            IARequestDTO dto

    ) {

        String respuesta =
                iaService.organizarDia(
                        dto.getMensaje()
                );

        return ResponseEntity.ok(

                IAChatResponseDTO.builder()
                        .respuesta(respuesta)
                        .build()

        );
    }

    /**
     * Analiza texto y devuelve una respuesta directa.
     */
    @PostMapping("/analizar")
    public ResponseEntity<?> analizar(
            @RequestBody Map<String, String> body
    ) {

        String mensaje = body.get("mensaje");

        String respuesta =
                iaService.analizarTareas(mensaje);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Interpreta texto y retorna recordatorios sugeridos.
     */
    @PostMapping("/interpretar")
    public List<IARecordatorioDTO> interpretar(
            @RequestBody IAMensajeDTO dto
    ) {

        return iaService.interpretarTareas(
                dto.getMensaje()
        );

    }

    /**
     * Interpreta texto, convierte a recordatorios y los guarda en BD.
     */
    @PostMapping("/crear-recordatorios")
    public ResponseEntity<?> crearRecordatoriosIA(

            @RequestBody IAMensajeDTO dto,

            Authentication authentication

    ) {

        /*
            IA interpreta tareas
         */
        List<IARecordatorioDTO> tareasIA =
                iaService.interpretarTareas(
                        dto.getMensaje()
                );

        /*
            Convertir a DTO normal
         */
        List<RecordatorioRequestDTO> tareas =
                iaService.convertirARecordatorios(
                        tareasIA
                );

        /*
            Guardar en BD
         */
        iaService.guardarRecordatoriosIA(
                tareas,
                authentication.getName()
        );

        return ResponseEntity.ok(
                tareas
        );
    }

    /**
     * Verifica conectividad con el proveedor de IA.
     */
    @GetMapping("/diagnostico")
    public ResponseEntity<?> diagnosticoOllama() {

        return ResponseEntity.ok(
                iaService.diagnosticarOllama()
        );
    }

    /**
     * Chat con memoria contextual del usuario.
     */
    @PostMapping("/chat")
    public ResponseEntity<IAChatResponseDTO> chat(
            @Valid @RequestBody IAChatRequestDTO dto,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                iaService.chatConMemoria(
                        dto,
                        authentication.getName()
                )
        );
    }

    /**
     * Genera plan diario con IA.
     */
    @PostMapping("/plan-diario")
    public ResponseEntity<IAChatResponseDTO> planDiario(
            @RequestBody IAChatRequestDTO dto,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                iaService.generarPlanDiario(
                        dto,
                        authentication.getName()
                )
        );
    }

    /**
     * Genera plan semanal con IA.
     */
    @PostMapping("/plan-semanal")
    public ResponseEntity<IAChatResponseDTO> planSemanal(
            @RequestBody IAChatRequestDTO dto,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                iaService.generarPlanSemanal(
                        dto,
                        authentication.getName()
                )
        );
    }

    /**
     * Prioriza tareas existentes segun contexto.
     */
    @PostMapping("/priorizar")
    public ResponseEntity<IAChatResponseDTO> priorizar(
            @RequestBody IAChatRequestDTO dto,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                iaService.priorizarTareas(
                        dto,
                        authentication.getName()
                )
        );
    }

    /**
     * Ejecuta acciones de tareas en lenguaje natural.
     */
    @PostMapping("/tareas-nl")
    public ResponseEntity<IATareaOperacionResultadoDTO> tareasNL(
            @RequestBody IAChatRequestDTO dto,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                iaService.procesarTareasNL(
                        dto,
                        authentication.getName()
                )
        );
    }
}