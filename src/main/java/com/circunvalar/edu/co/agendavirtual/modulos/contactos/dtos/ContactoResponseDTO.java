package com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ContactoResponseDTO {

    private UUID id;

    private UUID contactoId;

    private String nombreUsuario;

    private String correoElectronico;

    private String telefono;

    private Integer eventosEnComun;

    private Integer eventosInvitado;
}