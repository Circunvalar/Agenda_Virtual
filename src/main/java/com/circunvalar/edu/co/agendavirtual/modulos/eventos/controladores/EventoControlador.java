package com.circunvalar.edu.co.agendavirtual.modulos.eventos.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios.ContactosServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.Evento;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios.EventoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador MVC para CRUD de eventos.
 */
@Controller
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoControlador {

    private final EventoServicio eventoServicio;

    private final ContactosServicio contactosServicio;

    /**
     * Lista eventos del usuario y carga contactos para invitados.
     */
    @GetMapping
    public String listarEventos(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "eventos",
                eventoServicio.obtenerEventosDTOUsuario(
                        authentication.getName()
                )
        );

        model.addAttribute(
                "evento",
                new EventoRequestDTO()
        );

        model.addAttribute(
                "contactos",
                contactosServicio.obtenerContactos(
                        authentication.getName()
                )
        );

        return "dashboard/eventos";
    }

    /**
     * Crea un evento y redirige al listado.
     */
    @PostMapping
    public String crearEvento(
            @ModelAttribute EventoRequestDTO dto,
            Authentication authentication
    ) {

        eventoServicio.crearEvento(
                dto,
                authentication.getName()
        );

        return "redirect:/eventos";
    }

    /**
     * Elimina un evento del usuario.
     */
    @PostMapping("/delete/{id}")
    public String eliminarEvento(
            @PathVariable String id,
            Authentication authentication
    ) {

        eventoServicio.eliminarEvento(
                id,
                authentication.getName()
        );

        return "redirect:/eventos";
    }

    /**
     * Actualiza datos e invitados de un evento.
     */
    @PostMapping("/update/{id}")
    public String actualizarEvento(
            @PathVariable UUID id,
            @ModelAttribute Evento eventoActualizado,
            @RequestParam(required = false)
            List<UUID> invitadosIds,
            Authentication auth
    ) {

        eventoServicio.actualizarEvento(
                id,
                eventoActualizado,
                invitadosIds,
                auth.getName()
        );

        return "redirect:/eventos";
    }
}