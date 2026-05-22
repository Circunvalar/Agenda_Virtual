package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IATareaDTO {

    private String titulo;

    private String descripcion;

    private String prioridad;

    private String categoria;

    private Integer duracionMinutos;

    private String fechaSugerida;

}