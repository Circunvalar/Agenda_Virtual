package com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.Evento;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventoRepositorio
        extends JpaRepository<Evento, UUID> {

    List<Evento> findByCreador(Usuario creador);

    @Query("""
        SELECT e
        FROM Evento e
        WHERE e.creador.id = :usuarioId
    """)
    List<Evento> findByCreador(UUID usuarioId);

    /*
        Eventos creados por el usuario
        donde el contacto fue invitado
     */
    @Query("""
        SELECT COUNT(e)
        FROM Evento e
        JOIN e.invitados i
        WHERE e.creador.id = :usuarioId
        AND i.id = :contactoId
    """)
    Integer countEventosEnComun(
            @Param("usuarioId") UUID usuarioId,
            @Param("contactoId") UUID contactoId
    );

    /*
        Eventos creados por el contacto
        donde el usuario fue invitado
     */
    @Query("""
        SELECT COUNT(e)
        FROM Evento e
        JOIN e.invitados i
        WHERE e.creador.id = :contactoId
        AND i.id = :usuarioId
    """)
    Integer countEventosInvitado(
            @Param("usuarioId") UUID usuarioId,
            @Param("contactoId") UUID contactoId
    );

    @Query("""
        SELECT DISTINCT e
        FROM Evento e
        LEFT JOIN FETCH e.invitados i
        WHERE e.creador = :creador
    """)
    List<Evento> findByCreadorWithInvitados(@Param("creador") Usuario creador);

    @Query("""
        SELECT DISTINCT e
        FROM Evento e
        LEFT JOIN FETCH e.creador c
        WHERE (e.notificado = false OR e.notificado IS NULL)
        AND e.estado <> com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.EstadoEvento.CANCELADO
    """)
    List<Evento> findPendientesNotificacion();
}