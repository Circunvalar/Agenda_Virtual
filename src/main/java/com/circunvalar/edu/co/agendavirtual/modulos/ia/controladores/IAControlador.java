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
    public ResponseEntity<?> diagnosticoProveedor(){

        return ResponseEntity.ok(
                iaService.diagnosticarProveedor()
        );

    }
}