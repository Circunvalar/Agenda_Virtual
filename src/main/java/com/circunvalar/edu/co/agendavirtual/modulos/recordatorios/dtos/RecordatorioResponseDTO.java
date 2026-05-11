package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordatorioResponseDTO {

    private UUID id;

    private String titulo;

    private String mensaje;

    private LocalDateTime fechaRecordatorio;

    private Boolean repetitivo;

    private Integer intervaloHoras;

    private String creador;

    private List<String> invitados;
}