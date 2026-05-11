package com.circunvalar.edu.co.agendavirtual.modulos.autentificacion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorPrincipal {

    @GetMapping("/")
    public String index() {

        return "index/index";
    }

    @GetMapping("/login")
    public String login() {

        return "login/login";
    }

    @GetMapping("/register")
    public String register() {

        return "register/register";
    }

    @GetMapping("/dashboard")
    public String dashboard() {

        return "dashboard/dashboard";
    }
}