package com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades;

import com.circunvalar.edu.co.agendavirtual.compartido.entidades.EntidadBase;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tareas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarea extends EntidadBase {

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    private Boolean completada;

    private LocalDate fechaLimite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario creador;

    @ManyToMany
    @JoinTable(
            name = "tarea_invitados",
            joinColumns = @JoinColumn(name = "tarea_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> invitados;
}