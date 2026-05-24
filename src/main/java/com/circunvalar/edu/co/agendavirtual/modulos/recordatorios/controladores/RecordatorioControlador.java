package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios.ContactosServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios.RecordatorioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controlador MVC para CRUD de recordatorios.
 */
@Controller
@RequestMapping("/recordatorios")
@RequiredArgsConstructor
public class RecordatorioControlador {

    private final RecordatorioServicio recordatorioServicio;

    private final ContactosServicio contactosServicio;

    /**
     * Lista recordatorios y carga datos auxiliares para la vista.
     */
    @GetMapping
    public String listarRecordatorios(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "recordatorios",
                recordatorioServicio
                        .obtenerRecordatoriosUsuario(
                                authentication.getName()
                        )
        );

        model.addAttribute(
                "recordatorio",
                new RecordatorioRequestDTO()
        );

        model.addAttribute(
                "contactos",
                contactosServicio.obtenerContactos(
                        authentication.getName()
                )
        );

        return "dashboard/recordatorios";
    }

    /**
     * Crea un recordatorio y redirige al listado.
     */
    @PostMapping
    public String crearRecordatorio(
            @ModelAttribute RecordatorioRequestDTO dto,
            Authentication authentication
    ) {

        recordatorioServicio.crearRecordatorio(
                dto,
                authentication.getName()
        );

        return "redirect:/recordatorios";
    }

    /**
     * Actualiza un recordatorio existente.
     */
    @PostMapping("/update/{id}")
    public String actualizarRecordatorio(
            @PathVariable UUID id,
            @ModelAttribute RecordatorioRequestDTO dto,
            Authentication authentication
    ) {

        recordatorioServicio.actualizarRecordatorio(
                id,
                dto,
                authentication.getName()
        );

        return "redirect:/recordatorios";
    }

    /**
     * Marca el recordatorio como completado.
     */
    @PostMapping("/complete/{id}")
    public String completarRecordatorio(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        recordatorioServicio.marcarCompletado(
                id,
                authentication.getName()
        );

        return "redirect:/recordatorios";
    }

    /**
     * Archiva el recordatorio (eliminacion logica).
     */
    @PostMapping("/delete/{id}")
    public String eliminarRecordatorio(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        recordatorioServicio.archivarRecordatorio(
                id,
                authentication.getName()
        );

        return "redirect:/recordatorios";
    }
}