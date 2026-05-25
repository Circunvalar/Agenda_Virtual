package com.circunvalar.edu.co.agendavirtual.modulos.contactos.servicios;

import com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos.ContactoResponseDTO;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.dtos.ContactoResultado;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.entidades.Contactos;
import com.circunvalar.edu.co.agendavirtual.modulos.contactos.repositorios.ContactosRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.eventos.repositorios.EventoRepositorio;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.entidades.Usuario;
import com.circunvalar.edu.co.agendavirtual.modulos.usuarios.repositorios.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Logica de negocio para la gestion de contactos.
 */
@Service
@RequiredArgsConstructor
public class ContactosServicio {

    private final ContactosRepositorio contactosRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final EventoRepositorio eventoRepositorio;

    /**
     * Agrega un contacto por telefono y evita duplicados.
     */
    public ContactoResultado agregarContacto(
            String telefono,
            String username
    ) {

        if(telefono == null || telefono.isBlank()){
            return ContactoResultado.error(
                    "Debes ingresar un numero de telefono."
            );
        }

        String telefonoLimpio = telefono.replaceAll("\\D", "");

        if(telefonoLimpio.length() != 10){
            return ContactoResultado.error(
                    "El numero debe tener 10 digitos."
            );
        }

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        Usuario contactoUsuario = usuarioRepositorio
                .findByTelefono(telefonoLimpio)
                .orElse(null);

        if(contactoUsuario == null){
            return ContactoResultado.error(
                    "No se encontro un usuario con ese numero."
            );
        }

        // NO AGREGARSE A SI MISMO
        if(usuario.getId().equals(contactoUsuario.getId())){
            return ContactoResultado.error(
                    "No puedes agregarte a ti mismo como contacto."
            );
        }

        // EVITAR DUPLICADOS
        if(contactosRepositorio.existsByUsuarioAndContacto(
                usuario,
                contactoUsuario
        )){
            return ContactoResultado.error(
                    "Este contacto ya esta agregado."
            );
        }

        Contactos contacto = Contactos.builder()
                .usuario(usuario)
                .contacto(contactoUsuario)
                .build();

        contactosRepositorio.save(contacto);

        return ContactoResultado.ok(
                "Contacto agregado correctamente."
        );
    }

    /**
     * Obtiene contactos como entidades para uso interno.
     */
    public List<Contactos> obtenerContactos(
            String username
    ) {

        Usuario usuario = usuarioRepositorio
                .findByNombreDeUsuario(username)
                .orElseThrow();

        return contactosRepositorio.findByUsuario(usuario);
    }

    /**
     * Obtiene contactos en formato DTO para la UI.
     */
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

    /**
     * Convierte una relacion de contacto a DTO con metricas de eventos en comun.
     */
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
    /**
     * Elimina la relacion de contacto del usuario.
     */
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