package com.circunvalar.edu.co.agendavirtual.modulos.ia.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IAVistaControlador {

    @GetMapping("/ia")
    public String vistaIA() {

        return "ia/asistente-ia";

    }

}