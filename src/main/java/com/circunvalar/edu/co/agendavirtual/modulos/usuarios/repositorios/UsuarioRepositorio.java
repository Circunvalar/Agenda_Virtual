package com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepositorio extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByNombreDeUsuario(String nombreDeUsuario);
    Optional<Usuario> findByCorreoElectronico(String email);

    boolean existsByNombreDeUsuario(String username);
    boolean existsByCorreoElectronico(String email);
}