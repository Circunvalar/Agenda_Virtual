package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IARespuestaDTO {

    private List<IATareaDTO> tareas;

}