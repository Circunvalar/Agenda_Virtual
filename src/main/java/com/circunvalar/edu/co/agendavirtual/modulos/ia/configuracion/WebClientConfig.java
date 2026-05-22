package com.circunvalar.edu.co.agendavirtual.modulos.ia.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {

        return WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();

    }

}