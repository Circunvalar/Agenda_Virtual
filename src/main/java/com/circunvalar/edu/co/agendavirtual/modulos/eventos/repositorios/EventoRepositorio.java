package com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.eventos.entidades.Evento;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventoRepositorio
        extends JpaRepository<Evento, UUID> {

    List<Evento> findByCreador(Usuario creador);
    List<Evento> findByCreador(UUID creador);
}