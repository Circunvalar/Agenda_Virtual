package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RecordatorioRepositorio extends JpaRepository<Recordatorio, UUID> {

    List<Recordatorio> findByCreadorAndArchivadoFalseOrderByFechaLimiteAsc(
            Usuario creador
    );

    List<Recordatorio> findByArchivadoFalseAndCompletadoFalse();

    List<Recordatorio> findByNotificadoFalseAndArchivadoFalseAndCompletadoFalse();

    List<Recordatorio> findByFechaLimiteBeforeAndArchivadoFalse(
            LocalDateTime fecha
    );

}