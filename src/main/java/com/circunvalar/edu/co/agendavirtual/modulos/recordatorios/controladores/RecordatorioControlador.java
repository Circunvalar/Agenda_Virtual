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

@Controller
@RequestMapping("/recordatorios")
@RequiredArgsConstructor
public class RecordatorioControlador {

    private final RecordatorioServicio recordatorioServicio;

    private final ContactosServicio contactosServicio;

    /*
        LISTAR
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

    /*
        CREAR
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

    /*
        ACTUALIZAR
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

    /*
        COMPLETAR
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

    /*
        ELIMINAR LOGICO
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