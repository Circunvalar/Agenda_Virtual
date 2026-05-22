package com.circunvalar.edu.co.agendavirtual.modulos.calendario.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.calendario.dtos.CalendarioEventoDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios.EventoServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios.RecordatorioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CalendarioControlador {

    private final RecordatorioServicio recordatorioServicio;

    private final EventoServicio eventoServicio;

    private static final DateTimeFormatter ISO_DATE_TIME =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final DateTimeFormatter ISO_DATE =
            DateTimeFormatter.ISO_LOCAL_DATE;

    @GetMapping("/calendario")
    public String vistaCalendario() {
        return "dashboard/calendario";
    }

    @GetMapping("/calendario/eventos")
    @ResponseBody
    public List<CalendarioEventoDTO> obtenerEventosCalendario(
            Authentication authentication
    ) {

        String username = authentication.getName();

        List<CalendarioEventoDTO> eventos = new ArrayList<>();

        List<RecordatorioResponseDTO> recordatorios =
                recordatorioServicio.obtenerRecordatoriosUsuario(username);

        for (RecordatorioResponseDTO recordatorio : recordatorios) {
            LocalDateTime fecha = recordatorio.getFechaLimite();
            if (fecha == null) {
                continue;
            }
            eventos.add(
                    CalendarioEventoDTO.builder()
                            .id(String.valueOf(recordatorio.getId()))
                            .title(recordatorio.getTitulo())
                            .start(ISO_DATE_TIME.format(fecha))
                            .end(null)
                            .allDay(false)
                            .color(recordatorio.getColor() != null
                                    ? recordatorio.getColor()
                                    : "#6366f1")
                            .tipo("recordatorio")
                            .detalle(recordatorio.getMensaje())
                            .build()
            );
        }

        List<EventoResponseDTO> eventosUsuario =
                eventoServicio.obtenerEventosDTOUsuario(username);

        for (EventoResponseDTO evento : eventosUsuario) {
            LocalDate fechaInicio = evento.getFechaInicio();
            LocalDate fechaFin = evento.getFechaFin();

            if (fechaInicio == null) {
                continue;
            }

            boolean allDay = Boolean.TRUE.equals(evento.getTodoElDia());

            String start;
            String end = null;

            if (allDay) {
                start = ISO_DATE.format(fechaInicio);
                if (fechaFin != null) {
                    end = ISO_DATE.format(fechaFin);
                }
            } else {
                LocalTime horaInicio = evento.getHoraInicio();
                LocalTime horaFin = evento.getHoraFin();
                LocalDateTime inicioDT = LocalDateTime.of(
                        fechaInicio,
                        horaInicio != null ? horaInicio : LocalTime.MIN
                );
                start = ISO_DATE_TIME.format(inicioDT);

                if (fechaFin != null) {
                    LocalDateTime finDT = LocalDateTime.of(
                            fechaFin,
                            horaFin != null ? horaFin : LocalTime.MIN
                    );
                    end = ISO_DATE_TIME.format(finDT);
                }
            }

            eventos.add(
                    CalendarioEventoDTO.builder()
                            .id(String.valueOf(evento.getId()))
                            .title(evento.getTitulo())
                            .start(start)
                            .end(end)
                            .allDay(allDay)
                            .color(evento.getColor() != null
                                    ? evento.getColor()
                                    : "#22c55e")
                            .tipo("evento")
                            .detalle(evento.getUbicacion())
                            .build()
            );
        }

        return eventos;
    }
}

