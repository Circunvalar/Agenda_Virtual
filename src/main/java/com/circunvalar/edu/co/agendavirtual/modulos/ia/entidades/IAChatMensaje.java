package com.circunvalar.edu.co.agendavirtual.modulos.ia.entidades;

import com.circunvalar.edu.co.agendavirtual.compartido.entidades.EntidadBase;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "ia_chat_mensajes",
        indexes = {
                @Index(
                        name = "idx_ia_chat_usuario",
                        columnList = "usuario_id"
                ),
                @Index(
                        name = "idx_ia_chat_created",
                        columnList = "createdAt"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IAChatMensaje extends EntidadBase {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IAChatRol rol;

    @Column(nullable = false, length = 4000)
    private String contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}

