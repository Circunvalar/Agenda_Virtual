package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.controladores;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.dtos.RecordatorioRequestDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios.RecordatorioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/recordatorios")
@RequiredArgsConstructor
public class RecordatorioControlador {

    private final RecordatorioServicio recordatorioServicio;

    @GetMapping
    public String listarRecordatorios(
            Authentication authentication,
            Model model
    ) {

        model.addAttribute(
                "recordatorios",
                recordatorioServicio.obtenerRecordatoriosUsuario(
                        authentication.getName()
                )
        );

        return "recordatorios";
    }

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

    @PostMapping("/delete/{id}")
    public String eliminarRecordatorio(
            @PathVariable String id,
            Authentication authentication
    ) {

        recordatorioServicio.eliminarRecordatorio(
                id,
                authentication.getName()
        );

        return "redirect:/recordatorios";
    }
}