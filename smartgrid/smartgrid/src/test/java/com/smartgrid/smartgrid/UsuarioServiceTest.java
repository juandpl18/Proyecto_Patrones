package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.dto.CrearUsuarioRequest;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.UsuarioRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder encoder;

    private UsuarioService servicio;
    private Usuario admin;
    private Usuario operador;

    @BeforeEach
    void preparar() {
        servicio = new UsuarioService(
                repository, encoder, new AutorizacionService());

        admin = new Usuario("Admin", "admin", "x", Rol.ADMIN, true);
        ReflectionTestUtils.setField(admin, "id", 1L);

        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);
    }

    private CrearUsuarioRequest datos(
            String username, String password, String rol) {
        return new CrearUsuarioRequest("Operador Uno", username, password, rol);
    }

    private void assertEstado(HttpStatus esperado, Runnable accion) {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, accion::run);
        assertEquals(esperado, ex.getStatusCode());
    }

    @Test
    void adminCreaUsuarioConPasswordCifrada() {
        when(repository.existsByUsernameIgnoreCase("operador1"))
                .thenReturn(false);
        when(encoder.encode("clave1234")).thenReturn("HASH");
        when(repository.save(any(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Usuario creado = servicio.crear(
                admin, datos("operador1", "clave1234", "operador"));

        assertEquals("HASH", creado.getPassword());
        assertEquals(Rol.OPERADOR, creado.getRol());
        assertTrue(creado.isActivo());
    }

    @Test
    void operadorNoPuedeCrearUsuarios() {
        assertEstado(HttpStatus.FORBIDDEN, () ->
                servicio.crear(operador,
                        datos("otro", "clave1234", "OPERADOR")));

        verify(repository, never()).save(any());
    }

    @Test
    void rechazaUsernameDuplicado() {
        when(repository.existsByUsernameIgnoreCase("operador1"))
                .thenReturn(true);

        assertEstado(HttpStatus.CONFLICT, () ->
                servicio.crear(admin,
                        datos("operador1", "clave1234", "OPERADOR")));

        verify(repository, never()).save(any());
    }

    @Test
    void rechazaRolInvalido() {
        assertEstado(HttpStatus.BAD_REQUEST, () ->
                servicio.crear(admin,
                        datos("operador1", "clave1234", "SUPERUSER")));
    }

    @Test
    void rechazaPasswordCorta() {
        assertEstado(HttpStatus.BAD_REQUEST, () ->
                servicio.crear(admin,
                        datos("operador1", "123", "OPERADOR")));
    }

    @Test
    void adminNoPuedeDesactivarseASiMismo() {
        assertEstado(HttpStatus.BAD_REQUEST, () ->
                servicio.cambiarEstado(admin, 1L, false));
    }
}