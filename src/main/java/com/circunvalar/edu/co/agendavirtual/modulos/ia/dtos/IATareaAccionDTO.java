package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IATareaAccionDTO {

    private String accion;

    private String id;

    private String titulo;

    private String descripcion;

    private String prioridad;

    private String fechaLimite;
}

