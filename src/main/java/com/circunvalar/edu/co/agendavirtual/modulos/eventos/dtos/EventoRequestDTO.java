package com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.EstadoEvento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EventoRequestDTO {

    private String titulo;

    private String descripcion;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private Boolean todoElDia;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String ubicacion;

    private String color;

    private EstadoEvento estado;

    /*
     IDs de usuarios invitados
    */
    private List<UUID> invitadosIds;
}