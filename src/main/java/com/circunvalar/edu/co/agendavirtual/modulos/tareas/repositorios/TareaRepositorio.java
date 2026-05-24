package com.circunvalar.edu.co.agendavirtual.modulos.tareas.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.tareas.entidades.Tarea;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TareaRepositorio
        extends JpaRepository<Tarea, UUID> {

    List<Tarea> findByCreador(Usuario creador);
    List<Tarea> findByCreador(UUID creador);

    List<Tarea> findByCreadorAndTituloIgnoreCase(
            Usuario creador,
            String titulo
    );
}