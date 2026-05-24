package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.CategoriaRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.PrioridadRecordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.TipoRepeticion;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios.RecordatorioRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Logica de negocio para recordatorios y sus notificaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecordatorioServicio {

    private final RecordatorioRepositorio recordatorioRepositorio;

    private final UsuarioRepositorio usuarioRepositorio;

    /**
     * Crea un recordatorio y lo asocia al usuario autenticado.
     */
    public Recordatorio crearRecordatorio(
            RecordatorioRequestDTO dto,
            String username
    ) {

        Usuario creador = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Usuario> invitados = Collections.emptyList();

        if (
                dto.getInvitadosIds() != null
                        && !dto.getInvitadosIds().isEmpty()
        ) {

            invitados = usuarioRepositorio
                    .findAllById(dto.getInvitadosIds());

        }

        Recordatorio recordatorio = Recordatorio.builder()

                .titulo(dto.getTitulo())

                .mensaje(dto.getMensaje())

                .fechaLimite(dto.getFechaLimite())

                .recordarAntesMinutos(
                        dto.getRecordarAntesMinutos() != null
                                ? dto.getRecordarAntesMinutos()
                                : 30
                )

                .repetitivo(
                        dto.getRepetitivo() != null
                                ? dto.getRepetitivo()
                                : false
                )

                .tipoRepeticion(
                        dto.getTipoRepeticion() != null
                                ? dto.getTipoRepeticion()
                                : TipoRepeticion.SIN_REPETICION
                )

                .intervaloDias(
                        dto.getIntervaloDias()
                )

                .prioridad(
                        dto.getPrioridad() != null
                                ? dto.getPrioridad()
                                : PrioridadRecordatorio.MEDIA
                )

                .categoria(
                        dto.getCategoria() != null
                                ? dto.getCategoria()
                                : CategoriaRecordatorio.OTRO
                )

                .color(
                        dto.getColor() != null
                                ? dto.getColor()
                                : "#6366f1"
                )

                .completado(false)

                .archivado(false)

                .notificado(false)

                .ultimaNotificacion(null)

                .creador(creador)

                .invitados(invitados)

                .build();

        return recordatorioRepositorio.save(recordatorio);

    }

    /**
     * Lista recordatorios activos del usuario.
     */
    public List<RecordatorioResponseDTO> obtenerRecordatoriosUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Recordatorio> recordatorios =
                recordatorioRepositorio
                        .findByCreadorAndArchivadoFalseOrderByFechaLimiteAsc(
                                usuario
                        );

        return recordatorios.stream()
                .map(this::convertirDTO)
                .toList();

    }

    /**
     * Actualiza un recordatorio validando propiedad y valores por defecto.
     */
    public void actualizarRecordatorio(
            UUID id,
            RecordatorioRequestDTO dto,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Recordatorio recordatorio =
                recordatorioRepositorio
                        .findById(id)
                        .orElseThrow();

        validarPropietario(recordatorio, usuario);

        recordatorio.setTitulo(dto.getTitulo());

        recordatorio.setMensaje(dto.getMensaje());

        recordatorio.setFechaLimite(dto.getFechaLimite());

        // Evitar asignar valores nulos a columnas NOT NULL en la entidad.
        // Si el DTO no trae el campo (p. ej. checkbox no marcado), conservamos el valor existente
        // o aplicamos un default seguro.

        if (dto.getRecordarAntesMinutos() != null) {
            recordatorio.setRecordarAntesMinutos(dto.getRecordarAntesMinutos());
        }

        // Para checkbox 'repetitivo' tratamos la ausencia como 'false' (unchecked).
        boolean repet = dto.getRepetitivo() != null ? dto.getRepetitivo() : false;
        recordatorio.setRepetitivo(repet);

        if (dto.getTipoRepeticion() != null) {
            recordatorio.setTipoRepeticion(dto.getTipoRepeticion());
        } else if (!repet) {
            // si no es repetitivo, garantizamos SIN_REPETICION
            recordatorio.setTipoRepeticion(TipoRepeticion.SIN_REPETICION);
        }

        if (dto.getIntervaloDias() != null) {
            recordatorio.setIntervaloDias(dto.getIntervaloDias());
        } else if (!repet) {
            // si no es repetitivo, dejamos intervalo en 0 para no incumplir NOT NULL
            recordatorio.setIntervaloDias(0);
        }

        if (dto.getPrioridad() != null) {
            recordatorio.setPrioridad(dto.getPrioridad());
        }

        if (dto.getCategoria() != null) {
            recordatorio.setCategoria(dto.getCategoria());
        }

        if (dto.getColor() != null) {
            recordatorio.setColor(dto.getColor());
        }

        if (dto.getInvitadosIds() != null) {

            List<Usuario> invitados =
                    usuarioRepositorio
                            .findAllById(dto.getInvitadosIds());

            recordatorio.setInvitados(invitados);

        }

        recordatorioRepositorio.save(recordatorio);

    }

    /**
     * Marca el recordatorio como completado y calcula la siguiente fecha si aplica.
     */
    public void marcarCompletado(
            UUID id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Recordatorio recordatorio =
                recordatorioRepositorio
                        .findById(id)
                        .orElseThrow();

        validarPropietario(recordatorio, usuario);

        recordatorio.setCompletado(true);

        if (
                Boolean.TRUE.equals(
                        recordatorio.getRepetitivo()
                )
        ) {

            actualizarSiguienteFecha(recordatorio);

            recordatorio.setCompletado(false);

        } else {

            recordatorio.setArchivado(true);

        }

        recordatorioRepositorio.save(recordatorio);

    }

    /**
     * Archiva el recordatorio si el usuario es propietario.
     */
    public void archivarRecordatorio(
            UUID id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Recordatorio recordatorio =
                recordatorioRepositorio
                        .findById(id)
                        .orElseThrow();

        validarPropietario(recordatorio, usuario);

        recordatorio.setArchivado(true);

        recordatorioRepositorio.save(recordatorio);

    }

    /**
     * Obtiene recordatorios pendientes de notificacion.
     */
    public List<Recordatorio> obtenerPendientes() {

        return recordatorioRepositorio
                .findByNotificadoFalseAndArchivadoFalseAndCompletadoFalse();

    }

    /**
     * Verifica si debe notificarse el recordatorio y actualiza su estado.
     */
    public void procesarRecordatorio(
            Recordatorio recordatorio
    ) {

        LocalDateTime ahora = LocalDateTime.now();

        LocalDateTime fechaNotificacion =
                recordatorio.getFechaLimite()
                        .minusMinutes(
                                recordatorio.getRecordarAntesMinutos()
                        );

        if (
                ahora.isAfter(fechaNotificacion)
                        && !recordatorio.getNotificado()
        ) {

            log.info(
                    "NOTIFICAR RECORDATORIO: {}",
                    recordatorio.getTitulo()
            );

            recordatorio.setNotificado(true);

            recordatorio.setUltimaNotificacion(ahora);

            if (
                    Boolean.TRUE.equals(
                            recordatorio.getRepetitivo()
                    )
            ) {

                actualizarSiguienteFecha(recordatorio);

            } else {

                recordatorio.setArchivado(true);

            }

            recordatorioRepositorio.save(recordatorio);

        }

    }

    /**
     * Calcula la siguiente fecha cuando el recordatorio es repetitivo.
     */
    private void actualizarSiguienteFecha(
            Recordatorio recordatorio
    ) {

        switch (
                recordatorio.getTipoRepeticion()
        ) {

            case DIARIO ->

                    recordatorio.setFechaLimite(
                            recordatorio.getFechaLimite()
                                    .plusDays(1)
                    );

            case SEMANAL ->

                    recordatorio.setFechaLimite(
                            recordatorio.getFechaLimite()
                                    .plusWeeks(1)
                    );

            case MENSUAL ->

                    recordatorio.setFechaLimite(
                            recordatorio.getFechaLimite()
                                    .plusMonths(1)
                    );

            case PERSONALIZADO ->

                    recordatorio.setFechaLimite(
                            recordatorio.getFechaLimite()
                                    .plusDays(
                                            recordatorio.getIntervaloDias()
                                    )
                    );

        }

        recordatorio.setNotificado(false);

    }

    /**
     * Calcula el estado visual del recordatorio para la UI.
     */
    public String calcularEstadoVisual(
            Recordatorio recordatorio
    ) {

        if (recordatorio.getCompletado()) {

            return "COMPLETADO";

        }

        if (
                recordatorio.getFechaLimite()
                        .isBefore(LocalDateTime.now())
        ) {

            return "VENCIDO";

        }

        if (
                recordatorio.getFechaLimite()
                        .minusHours(1)
                        .isBefore(LocalDateTime.now())
        ) {

            return "PROXIMO";

        }

        return "PENDIENTE";

    }

    /**
     * Convierte una entidad a su DTO de respuesta.
     */
    private RecordatorioResponseDTO convertirDTO(
            Recordatorio recordatorio
    ) {

        return RecordatorioResponseDTO.builder()

                .id(recordatorio.getId())

                .titulo(recordatorio.getTitulo())

                .mensaje(recordatorio.getMensaje())

                .fechaLimite(recordatorio.getFechaLimite())

                .completado(recordatorio.getCompletado())

                .archivado(recordatorio.getArchivado())

                .repetitivo(recordatorio.getRepetitivo())

                .prioridad(recordatorio.getPrioridad())

                .categoria(recordatorio.getCategoria())

                .color(recordatorio.getColor())

                .estadoVisual(
                        calcularEstadoVisual(recordatorio)
                )
                .build();

    }

    /**
     * Valida que el usuario sea propietario del recordatorio.
     */
    private void validarPropietario(
            Recordatorio recordatorio,
            Usuario usuario
    ) {

        if (
                !recordatorio.getCreador()
                        .getId()
                        .equals(usuario.getId())
        ) {

            throw new RuntimeException(
                    "No autorizado"
            );

        }
    }
    /**
     * Guarda recordatorios generados por la IA para el usuario.
     */
    public void guardarRecordatoriosIA(
            List<RecordatorioRequestDTO> tareas,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Recordatorio> nuevos = tareas.stream()
                .map(dto -> Recordatorio.builder()
                        .titulo(dto.getTitulo())
                        .mensaje(dto.getMensaje())
                        .fechaLimite(dto.getFechaLimite())
                        .prioridad(dto.getPrioridad())
                        .categoria(dto.getCategoria())
                        .color("#6366f1")
                        .completado(false)
                        .archivado(false)
                        .repetitivo(false)
                        .creador(usuario)
                        .build())
                .toList();

        recordatorioRepositorio.saveAll(nuevos);
    }
}