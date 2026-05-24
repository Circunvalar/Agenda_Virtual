package com.circunvalar.edu.co.agendavirtual.modulos.autentificacion;

import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Rol;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para registro de usuarios.
 */
@Controller
@RequiredArgsConstructor
public class AutenticacionControlador {

    private final UsuarioRepositorio usuarioRepositorio;

    private final PasswordEncoder passwordEncoder;

    /**
     * Registra un usuario nuevo si no hay conflictos por username, email o telefono.
     */
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute RegisterRequest request
    ) {

        if (usuarioRepositorio.existsByNombreDeUsuario(request.getUsername())) {

            return "redirect:/register?usernameExists";
        }

        if (usuarioRepositorio.existsByCorreoElectronico(request.getEmail())) {

            return "redirect:/register?emailExists";
        }

        if (usuarioRepositorio.existsByTelefono(request.getTelefono())) {

            return "redirect:/register?phoneExists";
        }

        Usuario user = Usuario.builder()
                .nombreDeUsuario(request.getUsername())
                .correoElectronico(request.getEmail())
                .telefono(request.getTelefono())
                .contrasena(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .rol(Rol.ROLE_USUARIO)
                .build();

        usuarioRepositorio.save(user);

        return "redirect:/login?registered";
    }

    @Getter
    @Setter
    public static class RegisterRequest {

        @NotBlank
        private String username;

        @Email
        private String email;

        @NotBlank
        private String telefono;

        @NotBlank
        private String password;
    }
}