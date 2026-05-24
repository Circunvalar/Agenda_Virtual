package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAChatRequestDTO {

    @NotBlank
    private String mensaje;

    private String preferencias;

    private String modo;
}

