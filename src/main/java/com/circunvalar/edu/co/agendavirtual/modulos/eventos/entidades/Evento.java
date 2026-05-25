package com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades;

import com.circunvalar.edu.co.agendavirtual.compartido.entidades.EntidadBase;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "eventos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evento extends EntidadBase {

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean todoElDia;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String ubicacion;

    private String color;

    @Column
    @Builder.Default
    private Integer recordarAntesMinutos = 30;

    @Column
    @Builder.Default
    private Boolean notificado = false;

    private LocalDateTime ultimaNotificacion;

    @Enumerated(EnumType.STRING)
    private EstadoEvento estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario creador;

    @ManyToMany
    @JoinTable(
            name = "evento_invitados",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> invitados;

    public String getInvitadosIds() {

        if (invitados == null || invitados.isEmpty()) {
            return "";
        }

        return invitados.stream()
                .map(usuario -> usuario.getId().toString())
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }
}