package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IARequestDTO {

    @NotBlank
    private String mensaje;

}