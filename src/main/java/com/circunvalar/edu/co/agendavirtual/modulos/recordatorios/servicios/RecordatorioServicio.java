package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios.RecordatorioRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordatorioServicio {

    private final RecordatorioRepositorio recordatorioRepositorio;

    private final UsuarioRepositorio usuarioRepositorio;

    public Recordatorio crearRecordatorio(
            RecordatorioRequestDTO dto,
            String username
    ) {

        Usuario creador = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Usuario> invitados = usuarioRepositorio
                .findAllById(dto.getInvitadosIds());

        Recordatorio recordatorio = Recordatorio.builder()
                .titulo(dto.getTitulo())
                .mensaje(dto.getMensaje())
                .fechaRecordatorio(dto.getFechaRecordatorio())
                .repetitivo(dto.getRepetitivo())
                .intervaloHoras(dto.getIntervaloHoras())
                .creador(creador)
                .invitados(invitados)
                .build();

        return recordatorioRepositorio.save(recordatorio);
    }

    public List<Recordatorio> obtenerRecordatoriosUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        return recordatorioRepositorio.findByCreador(usuario);
    }

    public void eliminarRecordatorio(
            String id,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Recordatorio recordatorio = recordatorioRepositorio
                .findById(java.util.UUID.fromString(id))
                .orElseThrow();

        if (!recordatorio.getCreador()
                .getId()
                .equals(usuario.getId())) {

            throw new RuntimeException(
                    "No autorizado"
            );
        }

        recordatorioRepositorio.delete(recordatorio);
    }
    public List<RecordatorioResponseDTO>
    obtenerRecordatoriosDelUsuario(UUID usuarioId) {

        List<Recordatorio> recordatorios =
                recordatorioRepositorio.findByCreador(usuarioId);

        return recordatorios.stream()
                .map(this::convertirAResponseDTO)
                .toList();
    }
    private RecordatorioResponseDTO convertirAResponseDTO(
            Recordatorio recordatorio
    ) {

        return RecordatorioResponseDTO.builder()
                .id(recordatorio.getId())
                .titulo(recordatorio.getTitulo())
                .mensaje(recordatorio.getMensaje())
                .fechaRecordatorio(
                        recordatorio.getFechaRecordatorio()
                )
                .repetitivo(recordatorio.getRepetitivo())
                .intervaloHoras(
                        recordatorio.getIntervaloHoras()
                )
                .build();
    }
}