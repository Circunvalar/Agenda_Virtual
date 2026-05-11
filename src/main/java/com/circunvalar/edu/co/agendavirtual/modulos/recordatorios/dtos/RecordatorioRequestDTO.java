package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RecordatorioRequestDTO {

    private String titulo;

    private String mensaje;

    private LocalDateTime fechaRecordatorio;

    private Boolean repetitivo;

    private Integer intervaloHoras;

    /*
     IDs de usuarios invitados
    */
    private List<UUID> invitadosIds;
}