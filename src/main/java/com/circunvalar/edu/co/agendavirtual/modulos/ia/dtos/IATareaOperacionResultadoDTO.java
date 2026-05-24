package com.circunvalar.edu.co.agendavirtual.modulos.ia.dtos;

import com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos.TareaResponseDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IATareaOperacionResultadoDTO {

    @Builder.Default
    private List<TareaResponseDTO> creadas = new ArrayList<>();

    @Builder.Default
    private List<TareaResponseDTO> actualizadas = new ArrayList<>();

    @Builder.Default
    private List<String> completadas = new ArrayList<>();

    @Builder.Default
    private List<String> eliminadas = new ArrayList<>();

    @Builder.Default
    private List<String> advertencias = new ArrayList<>();
}

