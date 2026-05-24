package com.circunvalar.edu.co.agendavirtual.modulos.ia.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.ia.entidades.IAChatMensaje;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IAChatMensajeRepositorio
        extends JpaRepository<IAChatMensaje, UUID> {

    Page<IAChatMensaje> findByUsuarioOrderByCreatedAtDesc(
            Usuario usuario,
            Pageable pageable
    );
}

