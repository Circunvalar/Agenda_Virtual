package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.schedulers;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios.RecordatorioServicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecordatorioScheduler {

    private final RecordatorioServicio recordatorioServicio;

    @Scheduled(fixedRate = 60000)
    public void procesarRecordatorios() {

        List<Recordatorio> recordatorios =
                recordatorioServicio.obtenerPendientes();

        for (Recordatorio recordatorio : recordatorios) {

            recordatorioServicio.procesarRecordatorio(recordatorio);

        }

    }

}