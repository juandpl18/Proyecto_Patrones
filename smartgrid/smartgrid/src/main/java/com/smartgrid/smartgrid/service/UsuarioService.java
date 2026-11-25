package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CrearUsuarioRequest;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService {

    private static final int PASSWORD_MINIMA = 8;
    private static final String PATRON_USERNAME = "^[A-Za-z0-9._-]{3,50}$";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AutorizacionService autorizacionService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AutorizacionService autorizacionService) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.autorizacionService = autorizacionService;
    }

    /** Carga desde la BD al usuario dueño del token. */
    @Transactional(readOnly = true)
    public Usuario obtenerAutenticado(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "El usuario del token ya no existe"));
    }

    @Transactional
    public Usuario crear(Usuario solicitante, CrearUsuarioRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        validarDatos(datos);

        Rol rol = parsearRol(datos.rol());
        String username = datos.username().trim();

        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario con el username: " + username);
        }

        Usuario nuevo = new Usuario(
                datos.nombre().trim(),
                username,
                passwordEncoder.encode(datos.password()),
                rol,
                true);

        return usuarioRepository.save(nuevo);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar(Usuario solicitante) {
        autorizacionService.validarRol(solicitante, Rol.ADMIN);
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario cambiarEstado(
            Usuario solicitante, Long id, boolean activo) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (!activo && id.equals(solicitante.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puedes desactivar tu propio usuario");
        }

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Usuario no encontrado: " + id));

        usuario.setActivo(activo);
        return usuarioRepository.save(usuario);
    }

    private void validarDatos(CrearUsuarioRequest datos) {
        if (datos == null
                || esVacio(datos.nombre())
                || esVacio(datos.username())
                || esVacio(datos.password())
                || esVacio(datos.rol())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "nombre, username, password y rol son obligatorios");
        }

        if (!datos.username().trim().matches(PATRON_USERNAME)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El username debe tener de 3 a 50 caracteres: "
                            + "letras, números, punto, guion o guion bajo");
        }

        if (datos.password().length() < PASSWORD_MINIMA) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos "
                            + PASSWORD_MINIMA + " caracteres");
        }
    }

    private Rol parsearRol(String texto) {
        try {
            return Rol.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Rol inválido. Opciones: ADMIN u OPERADOR");
        }
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}