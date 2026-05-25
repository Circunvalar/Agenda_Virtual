package com.circunvalar.edu.co.agendavirtual.compartido.servicios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envia correos simples para notificaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServicio {

    private final JavaMailSender mailSender;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${app.mail.from:}")
    private String mailFrom;

    public boolean enviarCorreo(
            String destino,
            String asunto,
            String contenido
    ) {

        if(!mailEnabled){
            log.info("Envio de correo deshabilitado.");
            return false;
        }

        if(destino == null || destino.isBlank()){
            log.warn("Correo destino vacio, se omite el envio.");
            return false;
        }

        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destino);
            message.setSubject(asunto);
            message.setText(contenido);

            String from = (mailFrom != null && !mailFrom.isBlank())
                    ? mailFrom
                    : mailUsername;

            if(from != null && !from.isBlank()){
                message.setFrom(from);
            }

            mailSender.send(message);
            return true;

        }catch(Exception ex){
            log.error("Error enviando correo", ex);
            return false;
        }

    }
}

