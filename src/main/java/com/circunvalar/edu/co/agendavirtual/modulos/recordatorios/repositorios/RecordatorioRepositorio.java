package com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.repositorios;

import com.circunvalar.edu.co.agendavirtual.modulos.recordatorios.entidades.Recordatorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecordatorioRepositorio
        extends JpaRepository<Recordatorio, UUID> {

    List<Recordatorio> findByCreador(Usuario creador);
    List<Recordatorio> findByCreador(UUID creador);

}