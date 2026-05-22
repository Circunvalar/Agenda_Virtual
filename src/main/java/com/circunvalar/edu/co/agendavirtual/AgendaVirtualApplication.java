package com.circunvalar.edu.co.agendavirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AgendaVirtualApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgendaVirtualApplication.class, args);
    }

}
