package com.circunvalar.edu.co.agendavirtual.modulos.contactos.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios.ContactosServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos.ContactoResultado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

/**
 * Controlador MVC para gestionar contactos del usuario.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/contactos")
public class ContactosControlador {

    private final ContactosServicio contactoServicio;

    /**
     * Renderiza la vista de contactos.
     */
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

    /**
     * Agrega un contacto y muestra un mensaje flash.
     */
    @PostMapping("/agregar")
    public String agregarContacto(
            @RequestParam String telefono,
            Authentication auth,
            RedirectAttributes redirectAttributes
    ) {

        ContactoResultado resultado = contactoServicio.agregarContacto(
                telefono,
                auth.getName()
        );

        redirectAttributes.addFlashAttribute(
                "mensaje",
                resultado.getMensaje()
        );

        redirectAttributes.addFlashAttribute(
                "mensajeTipo",
                resultado.getTipo()
        );

        return "redirect:/contactos";
    }

    /**
     * Elimina un contacto del usuario.
     */
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