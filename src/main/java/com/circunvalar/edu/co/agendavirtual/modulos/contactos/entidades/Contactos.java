package com.circunvalar.edu.co.agendavirtual.modulos.contactos.entidades;

import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "contactos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contactos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /*
        Usuario dueño de la agenda
    */
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /*
        Usuario agregado como contacto
    */
    @ManyToOne
    @JoinColumn(name = "contacto_id")
    private Usuario contacto;
}