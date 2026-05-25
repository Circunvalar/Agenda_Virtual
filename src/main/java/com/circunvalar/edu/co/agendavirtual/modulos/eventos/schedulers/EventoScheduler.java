package com.circunvalar.edu.co.agendavirtual.modulos.eventos.schedulers;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios.EventoServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventoScheduler {

    private final EventoServicio eventoServicio;

    @Scheduled(fixedRate = 60000)
    public void procesarEventos() {
        eventoServicio.procesarNotificacionesEventos();
    }
}

