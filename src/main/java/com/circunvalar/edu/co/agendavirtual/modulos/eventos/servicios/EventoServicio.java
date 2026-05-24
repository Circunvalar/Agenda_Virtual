package com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.EstadoEvento;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.Evento;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios.EventoRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Logica de negocio para crear, actualizar y consultar eventos.
 */
@Service
@RequiredArgsConstructor
public class EventoServicio {

    private final EventoRepositorio eventoRepositorio;

    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Crea un evento para el usuario autenticado.
     */
    public Evento crearEvento(
            EventoRequestDTO dto,
            String username
    ) {

        Usuario creador = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Usuario> invitados = new ArrayList<>();

        if (
                dto.getInvitadosIds() != null &&
                        !dto.getInvitadosIds().isEmpty()
        ) {

            invitados = usuarioRepositorio
                    .findAllById(dto.getInvitadosIds());
        }

        Evento evento = Evento.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .todoElDia(
                        dto.getTodoElDia() != null
                                ? dto.getTodoElDia()
                                : false
                )
                .horaInicio(dto.getHoraInicio())
                .horaFin(dto.getHoraFin())
                .ubicacion(dto.getUbicacion())
                .color(
                        dto.getColor() != null
                                ? dto.getColor()
                                : "#6366f1"
                )
                .estado(
                        dto.getEstado() != null
                                ? dto.getEstado()
                                : EstadoEvento.PENDIENTE
                )
                .creador(creador)
                .invitados(invitados)
                .build();

        return eventoRepositorio.save(evento);
    }

    /**
     * Obtiene los eventos asociados al usuario.
     */
    public List<Evento> obtenerEventosUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        return eventoRepositorio.findByCreador(usuario);
    }

    /**
     * Elimina un evento si el usuario es el propietario.
     */
    public void eliminarEvento(
            String id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Evento evento = eventoRepositorio
                .findById(UUID.fromString(id))
                .orElseThrow();

        if (
                !evento.getCreador()
                        .getId()
                        .equals(usuario.getId())
        ) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        eventoRepositorio.delete(evento);
    }

    /**
     * Actualiza los datos y la lista de invitados de un evento.
     */
    public void actualizarEvento(
            UUID id,
            Evento eventoActualizado,
            List<UUID> invitadosIds,
            String username
    ) {

        Evento evento = eventoRepositorio
                .findById(id)
                .orElseThrow();

        evento.setTitulo(
                eventoActualizado.getTitulo()
        );

        evento.setDescripcion(
                eventoActualizado.getDescripcion()
        );

        evento.setFechaInicio(
                eventoActualizado.getFechaInicio()
        );

        evento.setFechaFin(
                eventoActualizado.getFechaFin()
        );

        evento.setHoraInicio(
                eventoActualizado.getHoraInicio()
        );

        evento.setHoraFin(
                eventoActualizado.getHoraFin()
        );

        evento.setTodoElDia(
                eventoActualizado.getTodoElDia()
        );

        evento.setUbicacion(
                eventoActualizado.getUbicacion()
        );

        evento.setColor(
                eventoActualizado.getColor()
        );

        if (invitadosIds != null) {

            List<Usuario> invitados =
                    usuarioRepositorio.findAllById(invitadosIds);

            evento.setInvitados(invitados);

        } else {

            evento.setInvitados(Collections.emptyList());

        }

        eventoRepositorio.save(evento);
    }
    /**
     * Retorna una lista de DTOs para mostrar en UI o calendario.
     */
    public List<EventoResponseDTO> obtenerEventosDTOUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Evento> eventos =
                eventoRepositorio.findByCreador(usuario);

        return eventos.stream()
                .map(evento -> EventoResponseDTO.builder()
                        .id(evento.getId())
                        .titulo(evento.getTitulo())
                        .descripcion(evento.getDescripcion())
                        .fechaInicio(evento.getFechaInicio())
                        .fechaFin(evento.getFechaFin())
                        .todoElDia(evento.getTodoElDia())
                        .horaInicio(evento.getHoraInicio())
                        .horaFin(evento.getHoraFin())
                        .ubicacion(evento.getUbicacion())
                        .color(evento.getColor())
                        .estado(evento.getEstado())
                        .build())
                .toList();
    }
}