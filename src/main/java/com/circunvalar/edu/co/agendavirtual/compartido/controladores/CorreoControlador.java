package com.circunvalar.edu.co.agendavirtual.compartido.controladores;

import com.circunvalar.edu.co.agendavirtual.compartido.dtos.CorreoPruebaRequestDTO;
import com.circunvalar.edu.co.agendavirtual.compartido.servicios.EmailServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/correos")
@RequiredArgsConstructor
public class CorreoControlador {

    private final EmailServicio emailServicio;
    private final UsuarioRepositorio usuarioRepositorio;

    @PostMapping("/prueba")
    public ResponseEntity<?> enviarCorreoPrueba(
            @RequestBody(required = false) CorreoPruebaRequestDTO dto,
            Authentication authentication
    ) {

        if (authentication == null) {
            return ResponseEntity.status(401)
                    .body(Map.of(
                            "ok", false,
                            "mensaje", "No autenticado."
                    ));
        }

        String destino = dto != null ? dto.getDestino() : null;

        if (destino == null || destino.isBlank()) {
            Usuario usuario = usuarioRepositorio
                    .findByNombreDeUsuario(authentication.getName())
                    .orElse(null);

            if (usuario == null
                    || usuario.getCorreoElectronico() == null
                    || usuario.getCorreoElectronico().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "ok", false,
                                "mensaje", "No se encontro correo para el usuario."
                        ));
            }

            destino = usuario.getCorreoElectronico();
        }

        String asunto = (dto != null
                && dto.getAsunto() != null
                && !dto.getAsunto().isBlank())
                ? dto.getAsunto()
                : "Prueba de correo - Agenda Virtual";

        String mensaje = (dto != null
                && dto.getMensaje() != null
                && !dto.getMensaje().isBlank())
                ? dto.getMensaje()
                : "Este es un correo de prueba para validar el envio desde Agenda Virtual.";

        boolean enviado = emailServicio.enviarCorreo(destino, asunto, mensaje);

        if (!enviado) {
            return ResponseEntity.status(500)
                    .body(Map.of(
                            "ok", false,
                            "mensaje", "No se pudo enviar el correo."
                    ));
        }

        return ResponseEntity.ok(Map.of(
                "ok", true,
                "destino", destino,
                "asunto", asunto
        ));
    }
}

