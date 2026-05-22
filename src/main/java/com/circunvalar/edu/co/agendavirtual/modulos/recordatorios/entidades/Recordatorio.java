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

    @Column(length = 2000)
    private String mensaje;

    @Column(nullable = false)
    private LocalDateTime fechaLimite;

    @Column(nullable = false)
    @Builder.Default
    private Boolean completado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean archivado = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean notificado = false;

    private LocalDateTime ultimaNotificacion;

    @Column(nullable = false)
    @Builder.Default
    private Integer recordarAntesMinutos = 30;

    @Column(nullable = false)
    @Builder.Default
    private Boolean repetitivo = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoRepeticion tipoRepeticion =
            TipoRepeticion.SIN_REPETICION;

    @Column(nullable = false)
    @Builder.Default
    private Integer intervaloDias = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadRecordatorio prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaRecordatorio categoria;

    private String color;

    private String archivoAdjunto;

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

    @PrePersist
    public void prePersist() {

        if(completado == null){
            completado = false;
        }

        if(archivado == null){
            archivado = false;
        }

        if(notificado == null){
            notificado = false;
        }

        if(repetitivo == null){
            repetitivo = false;
        }

        if(recordarAntesMinutos == null){
            recordarAntesMinutos = 30;
        }

        if(intervaloDias == null){
            intervaloDias = 0;
        }

        if(tipoRepeticion == null){
            tipoRepeticion =
                    TipoRepeticion.SIN_REPETICION;
        }

    }

}