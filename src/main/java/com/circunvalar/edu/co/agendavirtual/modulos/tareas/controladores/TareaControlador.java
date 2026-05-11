package com.circunvalar.edu.co.agendavirtual.modulos.tareas.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.tareas.dtos.TareaRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.servicios.TareaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tareas")
@RequiredArgsConstructor
public class TareaControlador {

    private final TareaServicio tareaServicio;

    @GetMapping
    public String listarTareas(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "tareas",
                tareaServicio.obtenerTareasUsuario(
                        authentication.getName()
                )
        );

        return "tareas";
    }

    @PostMapping
    public String crearTarea(
            @ModelAttribute TareaRequestDTO dto,
            Authentication authentication
    ) {

        tareaServicio.crearTarea(
                dto,
                authentication.getName()
        );

        return "redirect:/tareas";
    }

    @PostMapping("/complete/{id}")
    public String completarTarea(
            @PathVariable String id,
            Authentication authentication
    ) {

        tareaServicio.completarTarea(
                id,
                authentication.getName()
        );

        return "redirect:/tareas";
    }

    @PostMapping("/delete/{id}")
    public String eliminarTarea(
            @PathVariable String id,
            Authentication authentication
    ) {

        tareaServicio.eliminarTarea(
                id,
                authentication.getName()
        );

        return "redirect:/tareas";
    }
}