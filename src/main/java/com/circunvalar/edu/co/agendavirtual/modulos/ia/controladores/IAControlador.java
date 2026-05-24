package com.circunvalar.edu.co.agendavirtual.modulos.ia.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos.*;
import com.circunvalar.edu.co.agendavirtual.modulos.ia.servicios.IAService;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
@RequiredArgsConstructor
public class IAControlador {

    private final IAService iaService;

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

    @PostMapping("/organizar-dia")
    public ResponseEntity<IAChatResponseDTO>
    organizarDia(

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

    @PostMapping("/analizar")
    public ResponseEntity<?> analizar(
            @RequestBody Map<String, String> body
    ) {

        String mensaje = body.get("mensaje");

        String respuesta =
                iaService.analizarTareas(mensaje);

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/interpretar")
    public List<IARecordatorioDTO> interpretar(
            @RequestBody IAMensajeDTO dto
    ) {

        return iaService.interpretarTareas(
                dto.getMensaje()
        );

    }

    /*
        NUEVO ENDPOINT
        CREA Y GUARDA
        RECORDATORIOS AUTOMÁTICAMENTE
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

    @GetMapping("/diagnostico")
    public ResponseEntity<?> diagnosticoOllama() {

        return ResponseEntity.ok(
                iaService.diagnosticarOllama()
        );
    }

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