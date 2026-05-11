package com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.EstadoEvento;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResponseDTO {

    private UUID id;

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
}