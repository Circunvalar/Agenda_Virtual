package com.circunvalar.edu.co.agendavirtual.modulos.dashboard;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.servicios.EventoServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.servicios.RecordatorioServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.tareas.servicios.TareaServicio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.security.servicios.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Renderiza el dashboard con eventos, tareas y recordatorios del usuario.
 */
@Controller
@RequiredArgsConstructor
public class DashboardControlador {

    private final EventoServicio eventoServicio;
    private final TareaServicio tareaServicio;
    private final RecordatorioServicio recordatorioServicio;

    /**
     * Carga datos del usuario autenticado para el dashboard.
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {

        Usuario usuario = userDetails.getUser();

        model.addAttribute(
                "usuario",
                usuario
        );

        model.addAttribute(
                "eventos",
                eventoServicio.obtenerEventosUsuario(
                        usuario.getNombreDeUsuario()
                )
        );

        model.addAttribute(
                "tareas",
                tareaServicio.obtenerTareasUsuario(
                        usuario.getNombreDeUsuario()
                )
        );

        model.addAttribute(
                "recordatorios",
                recordatorioServicio.obtenerRecordatoriosUsuario(
                        usuario.getNombreDeUsuario()
                )
        );

        return "dashboard/dashboard";
    }
}