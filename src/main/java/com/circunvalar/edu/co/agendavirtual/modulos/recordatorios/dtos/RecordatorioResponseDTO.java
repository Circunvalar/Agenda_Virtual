package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.CategoriaRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.PrioridadRecordatorio;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class RecordatorioResponseDTO {

    private UUID id;

    private String titulo;

    private String mensaje;

    private LocalDateTime fechaLimite;

    private Boolean completado;

    private Boolean archivado;

    private Boolean repetitivo;

    // NUEVO
    private Integer intervaloDias;

    // NUEVO
    private LocalDateTime proximaRepeticion;

    private PrioridadRecordatorio prioridad;

    private Integer recordarAntesMinutos;

    private CategoriaRecordatorio categoria;

    private String color;

    private String estadoVisual;
}