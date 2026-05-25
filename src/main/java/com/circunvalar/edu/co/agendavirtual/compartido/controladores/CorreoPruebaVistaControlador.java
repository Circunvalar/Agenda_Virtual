package com.circunvalar.edu.co.agendavirtual.compartido.controladores;

import com.circunvalar.edu.co.agendavirtual.compartido.servicios.EmailServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.security.servicios.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/correo/prueba")
@RequiredArgsConstructor
public class CorreoPruebaVistaControlador {

    private final EmailServicio emailServicio;

    @GetMapping
    public String mostrarPagina(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {

        if (userDetails != null) {
            model.addAttribute("usuario", userDetails.getUser());
        }

        return "dashboard/correo-prueba";
    }

    @PostMapping("/enviar")
    public String enviarCorreoPrueba(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {

        if (userDetails == null) {
            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "No se pudo identificar el usuario en sesion."
            );
            redirectAttributes.addFlashAttribute("mensajeTipo", "error");
            return "redirect:/correo/prueba";
        }

        Usuario usuario = userDetails.getUser();
        String destino = usuario != null ? usuario.getCorreoElectronico() : null;

        if (destino == null || destino.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "No tienes un correo configurado en tu perfil."
            );
            redirectAttributes.addFlashAttribute("mensajeTipo", "error");
            return "redirect:/correo/prueba";
        }

        boolean enviado = emailServicio.enviarCorreo(
                destino,
                "Prueba de correo - Agenda Virtual",
                "Este es un correo de prueba enviado desde Agenda Virtual."
        );

        if (enviado) {
            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Correo enviado correctamente a " + destino
            );
            redirectAttributes.addFlashAttribute("mensajeTipo", "success");
        } else {
            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "No se pudo enviar el correo. Revisa la configuracion SMTP."
            );
            redirectAttributes.addFlashAttribute("mensajeTipo", "error");
        }

        return "redirect:/correo/prueba";
    }
}

