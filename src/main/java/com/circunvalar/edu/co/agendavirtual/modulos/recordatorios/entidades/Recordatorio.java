package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades;

import com.circunvalar.edu.co.agendavirtual.compartido.entidades.EntidadBase;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "recordatorios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recordatorio extends EntidadBase {

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1500)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaRecordatorio;

    private Boolean repetitivo;

    private Integer intervaloHoras;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario creador;

    @ManyToMany
    @JoinTable(
            name = "recordatorio_invitados",
            joinColumns = @JoinColumn(name = "recordatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> invitados;
}