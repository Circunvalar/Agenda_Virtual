package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RecordatorioRepositorio extends JpaRepository<Recordatorio, UUID> {

    List<Recordatorio> findByCreadorAndArchivadoFalseOrderByFechaLimiteAsc(
            Usuario creador
    );

    @Query("""
            SELECT DISTINCT r
            FROM Recordatorio r
            LEFT JOIN FETCH r.invitados i
            WHERE r.creador = :creador
              AND r.archivado = false
            ORDER BY r.fechaLimite ASC
            """)
    List<Recordatorio> findByCreadorWithInvitados(
            @Param("creador") Usuario creador
    );

    List<Recordatorio> findByArchivadoFalseAndCompletadoFalse();

    List<Recordatorio> findByNotificadoFalseAndArchivadoFalseAndCompletadoFalse();

    @Query("""
            SELECT r
            FROM Recordatorio r
            LEFT JOIN FETCH r.creador c
            WHERE (r.notificado = false OR r.notificado IS NULL)
              AND r.archivado = false
              AND r.completado = false
            """)
    List<Recordatorio> findPendientesNotificacionWithCreador();

    @Query("""
            SELECT DISTINCT r
            FROM Recordatorio r
            LEFT JOIN FETCH r.invitados i
            WHERE r.creador = :creador
              AND r.archivado = false
              AND r.fechaLimite >= :desde
            ORDER BY r.fechaLimite ASC
            """)
    List<Recordatorio> findVisiblesWithInvitados(
            @Param("creador") Usuario creador,
            @Param("desde") LocalDateTime desde
    );

    List<Recordatorio> findByFechaLimiteBeforeAndArchivadoFalse(
            LocalDateTime fecha
    );

}