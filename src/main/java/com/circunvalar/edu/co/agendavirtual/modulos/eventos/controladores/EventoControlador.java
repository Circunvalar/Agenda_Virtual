package com.circunvalar.edu.co.agendavirtual.modulos.eventos.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.dtos.EventoRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios.EventoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/eventos")
@RequiredArgsConstructor
public class EventoControlador {

    private final EventoServicio eventoServicio;

    @GetMapping
    public String listarEventos(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "eventos",
                eventoServicio.obtenerEventosUsuario(
                        authentication.getName()
                )
        );

        model.addAttribute(
                "evento",
                new EventoRequestDTO()
        );

        return "dashboard/eventos";
    }

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
}