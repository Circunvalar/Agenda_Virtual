package com.circunvalar.edu.co.agendavirtual.modulos.calendario.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarioEventoDTO {

    private String id;

    private String title;

    private String start;

    private String end;

    private Boolean allDay;

    private String color;

    private String tipo;

    private String detalle;

}

