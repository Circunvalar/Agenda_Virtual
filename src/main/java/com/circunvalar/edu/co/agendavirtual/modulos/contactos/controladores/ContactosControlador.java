package com.circunvalar.edu.co.agendavirtual.modulos.contactos.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios.ContactosServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/contactos")
public class ContactosControlador {

    private final ContactosServicio contactoServicio;

    @GetMapping
    public String contactos(
            Model model,
            Authentication auth
    ) {

        model.addAttribute(
                "contactos",
                contactoServicio.obtenerContactosUsuario(
                        auth.getName()
                )
        );

        return "dashboard/contactos";
    }

    @PostMapping("/agregar")
    public String agregarContacto(
            @RequestParam String telefono,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {

        String mensaje = contactoServicio.agregarContacto(
                telefono,
                auth.getName()
        );

        redirectAttributes.addFlashAttribute(
                "mensaje",
                mensaje
        );

        return "redirect:/contactos";
    }

    @PostMapping("/eliminar/{contactoId}")
    public String eliminarContacto(
            @PathVariable UUID contactoId,
            Authentication auth
    ) {

        contactoServicio.eliminarContacto(
                contactoId,
                auth.getName()
        );

        return "redirect:/contactos";
    }
}