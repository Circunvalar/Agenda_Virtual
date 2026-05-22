package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.CategoriaRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.PrioridadRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.TipoRepeticion;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAResponseDTO {

    private String titulo;

    private String mensaje;

    private String fechaLimite;

    private Integer recordarAntesMinutos;

    private Boolean repetitivo;

    private TipoRepeticion tipoRepeticion;

    private Integer intervaloDias;

    private PrioridadRecordatorio prioridad;

    private CategoriaRecordatorio categoria;

    private String color;

}