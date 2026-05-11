package com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TareaResponseDTO {

    private UUID id;

    private String titulo;

    private String descripcion;

    private String prioridad;

    private Boolean completada;

    private LocalDate fechaLimite;

    private String creador;

    private List<String> invitados;
}