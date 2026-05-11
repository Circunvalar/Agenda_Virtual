package com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades;

import com.circunvalar.edu.co.agendavirtual.compartido.entidades.EntidadBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "usuarios",
        indexes = {
                @Index(
                        name = "idx_user_username",
                        columnList = "nombre_de_usuario"
                ),
                @Index(
                        name = "idx_user_email",
                        columnList = "correo_electronico"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends EntidadBase {

    @Column(nullable = false, unique = true, length = 50)
    private String nombreDeUsuario;

    @Column(nullable = false, unique = true, length = 120)
    private String correoElectronico;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Rol rol;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonExpired = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean credentialsNonExpired = true;
}