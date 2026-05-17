package com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos.ContactoResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.entidades.Contactos;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.repositorios.ContactosRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios.EventoRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContactosServicio {

    private final ContactosRepositorio contactosRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final EventoRepositorio eventoRepositorio;

    public String agregarContacto(
            String telefono,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Usuario contactoUsuario = usuarioRepositorio
                .findByTelefono(telefono)
                .orElseThrow();

        // NO AGREGARSE A SI MISMO
        if(usuario.getId().equals(contactoUsuario.getId())){
            return "No puedes agregarte a ti mismo como contacto.";
        }

        // EVITAR DUPLICADOS
        if(contactosRepositorio.existsByUsuarioAndContacto(
                usuario,
                contactoUsuario
        )){
            return "Este contacto ya está agregado.";
        }

        Contactos contacto = Contactos.builder()
                .usuario(usuario)
                .contacto(contactoUsuario)
                .build();

        contactosRepositorio.save(contacto);

        return "Contacto agregado correctamente.";
    }

    public List<Contactos> obtenerContactos(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        return contactosRepositorio.findByUsuario(usuario);
    }

    public List<ContactoResponseDTO> obtenerContactosUsuario(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        List<Contactos> contactos =
                contactosRepositorio.findByUsuario(usuario);

        return contactos.stream()
                .map(contacto ->
                        convertirAResponseDTO(
                                contacto,
                                usuario
                        )
                )
                .toList();
    }

    private ContactoResponseDTO convertirAResponseDTO(
            Contactos contacto,
            Usuario usuario
    ) {

        Usuario usuarioContacto =
                contacto.getContacto();

        Integer eventosEnComun =
                eventoRepositorio.countEventosEnComun(
                        usuario.getId(),
                        usuarioContacto.getId()
                );

        Integer eventosInvitado =
                eventoRepositorio.countEventosInvitado(
                        usuario.getId(),
                        usuarioContacto.getId()
                );

        return ContactoResponseDTO.builder()
                .id(contacto.getId())
                .contactoId(usuarioContacto.getId())
                .nombreUsuario(
                        usuarioContacto.getNombreDeUsuario()
                )
                .correoElectronico(
                        usuarioContacto.getCorreoElectronico()
                )
                .telefono(
                        usuarioContacto.getTelefono()
                )
                .eventosEnComun(eventosEnComun)
                .eventosInvitado(eventosInvitado)
                .build();
    }
    public void eliminarContacto(
            UUID contactoId,
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Usuario contacto = usuarioRepositorio
                .findById(contactoId)
                .orElseThrow();

        Contactos relacion = contactosRepositorio
                .findByUsuarioAndContacto(
                        usuario,
                        contacto
                )
                .orElseThrow();

        contactosRepositorio.delete(relacion);
    }
}