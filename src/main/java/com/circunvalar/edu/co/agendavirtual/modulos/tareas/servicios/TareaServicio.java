package com.circunvalar.edu.co.agendavirtual.modulos.tareas.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos.TareaRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos.TareaResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades.Tarea;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.repositorios.TareaRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TareaServicio {

    private final TareaRepositorio tareaRepositorio;

    private final UsuarioRepositorio usuarioRepositorio;

    public Tarea crearTarea(
            TareaRequestDTO dto,
            String username
    ) {

        Usuario creador = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Usuario> invitados = usuarioRepositorio
                .findAllById(dto.getInvitadosIds());

        Tarea tarea = Tarea.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .prioridad(dto.getPrioridad())
                .fechaLimite(dto.getFechaLimite())
                .completada(false)
                .creador(creador)
                .invitados(invitados)
                .build();

        return tareaRepositorio.save(tarea);
    }

    public List<Tarea> obtenerTareasUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        return tareaRepositorio.findByCreador(usuario);
    }

    public void completarTarea(
            String id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Tarea tarea = tareaRepositorio
                .findById(java.util.UUID.fromString(id))
                .orElseThrow();

        if (!tarea.getCreador()
                .getId()
                .equals(usuario.getId())) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        tarea.setCompletada(true);

        tareaRepositorio.save(tarea);
    }

    public void eliminarTarea(
            String id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Tarea tarea = tareaRepositorio
                .findById(java.util.UUID.fromString(id))
                .orElseThrow();

        if (!tarea.getCreador()
                .getId()
                .equals(usuario.getId())) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        tareaRepositorio.delete(tarea);
    }
    public List<TareaResponseDTO> obtenerTareasDelUsuario(UUID usuarioId) {

        List<Tarea> tareas =
                tareaRepositorio.findByCreador(usuarioId);

        return tareas.stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }
    private TareaResponseDTO convertirAResponseDTO(Tarea tarea) {

        return TareaResponseDTO.builder()
                .id(tarea.getId())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .prioridad(String.valueOf(tarea.getPrioridad()))
                .completada(tarea.getCompletada())
                .fechaLimite(tarea.getFechaLimite())
                .build();
    }
}