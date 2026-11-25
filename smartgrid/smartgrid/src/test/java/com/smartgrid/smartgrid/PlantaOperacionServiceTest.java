package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.model.*;
import com.smartgrid.smartgrid.repository.AlertaRepository;
import com.smartgrid.smartgrid.repository.CambioEstadoPlantaRepository;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.PlantaOperacionService;
import com.smartgrid.smartgrid.state.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantaOperacionServiceTest {

    @Mock
    private PlantaGeneracionRepository plantas;

    @Mock
    private ZonaRedRepository zonas;

    @Mock
    private CambioEstadoPlantaRepository historial;

    @Mock
    private AlertaRepository alertas;

    private PlantaOperacionService servicio;
    private Usuario admin;
    private Usuario operador;

    @BeforeEach
    void preparar() {
        EstadoPlantaResolver resolver = new EstadoPlantaResolver(
                List.of(new ActivaState(), new MantenimientoState(),
                        new FueraDeServicioState()));

        servicio = new PlantaOperacionService(plantas, zonas, historial,
                alertas, resolver, new AutorizacionService());

        admin = new Usuario("Admin", "admin", "x", Rol.ADMIN, true);
        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);
    }

    private void plantaEn(EstadoPlanta estado) {
        PlantaGeneracion p = new PlantaGeneracion();
        ReflectionTestUtils.setField(p, "id", 5L);
        p.setNombre("Solar Norte");
        p.setTipoFuente(TipoFuente.SOLAR);
        p.setCapacidadKw(new BigDecimal("800"));
        p.setEstado(estado);
        when(plantas.findById(5L)).thenReturn(Optional.of(p));
    }

    private void assertEstado(HttpStatus esperado, Runnable accion) {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, accion::run);
        assertEquals(esperado, ex.getStatusCode());
    }

    @Test
    void activaPasaAMantenimientoYQuedaEnElHistorial() {
        plantaEn(EstadoPlanta.ACTIVA);

        var respuesta = servicio.cambiarEstado(
                admin, 5L, "mantenimiento", "Limpieza de paneles");

        assertEquals("MANTENIMIENTO", respuesta.estado());

        ArgumentCaptor<CambioEstadoPlanta> captor =
                ArgumentCaptor.forClass(CambioEstadoPlanta.class);
        verify(historial).save(captor.capture());
        assertEquals(EstadoPlanta.ACTIVA, captor.getValue().getEstadoAnterior());
        assertEquals("admin", captor.getValue().getUsuario());
        verify(alertas, never()).save(any());
    }

    @Test
    void salirDeServicioGeneraAlertaCritica() {
        plantaEn(EstadoPlanta.ACTIVA);

        servicio.cambiarEstado(admin, 5L, "FUERA_DE_SERVICIO", "Falla de inversor");

        ArgumentCaptor<Alerta> captor = ArgumentCaptor.forClass(Alerta.class);
        verify(alertas).save(captor.capture());
        assertEquals(TipoAlerta.FALLA_PLANTA, captor.getValue().getTipo());
        assertEquals(Severidad.CRITICA, captor.getValue().getSeveridad());
    }

    @Test
    void salirDeServicioExigeMotivo() {
        plantaEn(EstadoPlanta.ACTIVA);

        assertEstado(HttpStatus.BAD_REQUEST, () ->
                servicio.cambiarEstado(admin, 5L, "FUERA_DE_SERVICIO", "  "));

        verify(historial, never()).save(any());
    }

    @Test
    void fueraDeServicioNoPuedeVolverDirectoAActiva() {
        plantaEn(EstadoPlanta.FUERA_DE_SERVICIO);

        assertEstado(HttpStatus.CONFLICT, () ->
                servicio.cambiarEstado(admin, 5L, "ACTIVA", null));

        verify(historial, never()).save(any());
    }

    @Test
    void rechazaUnEstadoInexistente() {
        assertEstado(HttpStatus.BAD_REQUEST, () ->
                servicio.cambiarEstado(admin, 5L, "APAGADA", null));
    }

    @Test
    void operadorNoPuedeCambiarElEstado() {
        assertEstado(HttpStatus.FORBIDDEN, () ->
                servicio.cambiarEstado(operador, 5L, "MANTENIMIENTO", null));

        verify(plantas, never()).save(any());
    }
}