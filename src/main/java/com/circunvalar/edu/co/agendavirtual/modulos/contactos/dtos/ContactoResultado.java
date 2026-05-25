package com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos;

import lombok.Getter;

/**
 * Resultado simple para mostrar mensajes en la UI de contactos.
 */
@Getter
public class ContactoResultado {

    private final String mensaje;

    private final String tipo;

    private ContactoResultado(
            String mensaje,
            String tipo
    ) {
        this.mensaje = mensaje;
        this.tipo = tipo;
    }

    public static ContactoResultado ok(
            String mensaje
    ) {
        return new ContactoResultado(mensaje, "success");
    }

    public static ContactoResultado error(
            String mensaje
    ) {
        return new ContactoResultado(mensaje, "error");
    }
}

