package com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.EstadoEvento;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.Evento;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios.EventoRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import com.circunvalar.edu.co.agendavirtual.compartido.servicios.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    private final EmailServicio emailServicio;

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
                .recordarAntesMinutos(
                        dto.getRecordarAntesMinutos() != null
                                ? dto.getRecordarAntesMinutos()
                                : 30
                )
                .notificado(false)
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

        if (eventoActualizado.getRecordarAntesMinutos() != null) {
            evento.setRecordarAntesMinutos(
                    eventoActualizado.getRecordarAntesMinutos()
            );
        }

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
                eventoRepositorio.findByCreadorWithInvitados(usuario);

        actualizarEstadosAutomaticos(eventos);

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
                        .recordarAntesMinutos(evento.getRecordarAntesMinutos())
                        .estado(evento.getEstado())
                        .invitadosIds(
                                evento.getInvitados() == null
                                        ? List.of()
                                        : evento.getInvitados().stream()
                                        .map(Usuario::getId)
                                        .toList()
                        )
                        .build())
                .toList();
    }

    /**
     * Actualiza estados automaticos si corresponde (no sobrescribe CANCELADO/CONFIRMADO).
     */
    private void actualizarEstadosAutomaticos(
            List<Evento> eventos
    ) {

        if(eventos == null || eventos.isEmpty()){
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        List<Evento> actualizados = new ArrayList<>();

        for (Evento evento : eventos) {

            EstadoEvento estadoActual = evento.getEstado();

            if (estadoActual == EstadoEvento.CANCELADO
                    || estadoActual == EstadoEvento.CONFIRMADO) {
                continue;
            }

            EstadoEvento nuevoEstado = calcularEstadoAutomatico(
                    evento,
                    ahora
            );

            if (nuevoEstado != null && nuevoEstado != estadoActual) {
                evento.setEstado(nuevoEstado);
                actualizados.add(evento);
            }
        }

        if(!actualizados.isEmpty()){
            eventoRepositorio.saveAll(actualizados);
        }
    }

    private EstadoEvento calcularEstadoAutomatico(
            Evento evento,
            LocalDateTime ahora
    ) {

        if (evento.getFechaInicio() == null || evento.getFechaFin() == null) {
            return evento.getEstado();
        }

        LocalDateTime inicio = obtenerFechaInicio(evento);
        LocalDateTime fin = obtenerFechaFin(evento);

        if (inicio.isAfter(ahora)) {
            return EstadoEvento.PENDIENTE;
        }

        if (fin.isBefore(ahora)) {
            return EstadoEvento.FINALIZADO;
        }

        return EstadoEvento.EN_PROGRESO;
    }

    private LocalDateTime obtenerFechaInicio(
            Evento evento
    ) {

        if (Boolean.TRUE.equals(evento.getTodoElDia())) {
            return evento.getFechaInicio().atStartOfDay();
        }

        return evento.getFechaInicio().atTime(
                evento.getHoraInicio() != null
                        ? evento.getHoraInicio()
                        : java.time.LocalTime.of(0, 0)
        );
    }

    private LocalDateTime obtenerFechaFin(
            Evento evento
    ) {

        if (Boolean.TRUE.equals(evento.getTodoElDia())) {
            return evento.getFechaFin().atTime(23, 59);
        }

        return evento.getFechaFin().atTime(
                evento.getHoraFin() != null
                        ? evento.getHoraFin()
                        : java.time.LocalTime.of(23, 59)
        );
    }

    /**
     * Procesa eventos pendientes para enviar notificaciones por correo.
     */
    public void procesarNotificacionesEventos() {

        List<Evento> pendientes =
                eventoRepositorio.findPendientesNotificacion();

        if(pendientes == null || pendientes.isEmpty()){
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();

        for (Evento evento : pendientes) {

            LocalDateTime notificarEn = calcularFechaNotificacion(evento);
            LocalDateTime inicio = obtenerFechaInicio(evento);
            LocalDateTime fin = obtenerFechaFin(evento);

            if (notificarEn == null) {
                continue;
            }

            boolean dentroVentana =
                    (ahora.isAfter(notificarEn) || ahora.isEqual(notificarEn))
                            && (ahora.isBefore(fin) || ahora.isEqual(fin));

            if (!dentroVentana) {
                continue;
            }

            Usuario creador = evento.getCreador();
            String correo = creador != null
                    ? creador.getCorreoElectronico()
                    : null;

            String asunto = "Recordatorio de evento: " + evento.getTitulo();
            String contenido = construirMensajeEvento(evento, inicio);

            boolean enviado = emailServicio.enviarCorreo(
                    correo,
                    asunto,
                    contenido
            );

            if (enviado) {
                evento.setNotificado(true);
                evento.setUltimaNotificacion(ahora);
                eventoRepositorio.save(evento);
            }
        }
    }

    private LocalDateTime calcularFechaNotificacion(
            Evento evento
    ) {

        if (evento.getFechaInicio() == null) {
            return null;
        }

        if (Boolean.TRUE.equals(evento.getTodoElDia())) {
            return evento.getFechaInicio()
                    .minusDays(1)
                    .atTime(20, 0);
        }

        int minutosAntes = evento.getRecordarAntesMinutos() != null
                ? evento.getRecordarAntesMinutos()
                : 30;

        LocalDateTime inicio = obtenerFechaInicio(evento);
        return inicio.minusMinutes(minutosAntes);
    }

    private String construirMensajeEvento(
            Evento evento,
            LocalDateTime inicio
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("Hola, tienes un evento programado.\n\n");
        sb.append("Titulo: ").append(evento.getTitulo()).append("\n");

        if(evento.getDescripcion() != null && !evento.getDescripcion().isBlank()){
            sb.append("Descripcion: ").append(evento.getDescripcion()).append("\n");
        }

        sb.append("Fecha inicio: ")
                .append(inicio.toString())
                .append("\n");

        if (evento.getUbicacion() != null && !evento.getUbicacion().isBlank()) {
            sb.append("Ubicacion: ").append(evento.getUbicacion()).append("\n");
        }

        return sb.toString();
    }
}