package com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades.Prioridad;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TareaRequestDTO {

    private String titulo;

    private String descripcion;

    private Prioridad prioridad;

    private LocalDate fechaLimite;

    /*
     IDs de usuarios invitados
    */
    private List<UUID> invitadosIds;
}