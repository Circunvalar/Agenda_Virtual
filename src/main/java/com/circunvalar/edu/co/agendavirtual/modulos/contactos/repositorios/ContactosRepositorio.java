package com.circunvalar.edu.co.agendavirtual.modulos.contactos.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.entidades.Contactos;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactosRepositorio
        extends JpaRepository<Contactos, UUID> {

    List<Contactos> findByUsuario(Usuario usuario);

    boolean existsByUsuarioAndContacto(
            Usuario usuario,
            Usuario contacto
    );
    Optional<Contactos> findByUsuarioAndContacto(
            Usuario usuario,
            Usuario contacto
    );
}