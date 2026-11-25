package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.balanceo.AnalisisBalance;
import com.smartgrid.smartgrid.balanceo.BalanceoPlanner;
import com.smartgrid.smartgrid.balanceo.PlanCompleto;
import com.smartgrid.smartgrid.balanceo.ZonaAnalizada;
import com.smartgrid.smartgrid.command.balanceo.AccionBalanceoCommand;
import com.smartgrid.smartgrid.command.balanceo.AccionBalanceoFactory;
import com.smartgrid.smartgrid.command.balanceo.BalanceoInvoker;
import com.smartgrid.smartgrid.model.*;
import com.smartgrid.smartgrid.repository.AccionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EjecucionBalanceoRepository;
import com.smartgrid.smartgrid.repository.EventoDemandaRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.BalanceService;
import com.smartgrid.smartgrid.service.BalanceoService;
import com.smartgrid.smartgrid.strategy.CondicionesProduccion;
import com.smartgrid.smartgrid.strategy.balanceo.TransferenciaPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceoServiceTest {

    private static final Instant HASTA = Instant.parse("2026-11-21T11:00:00Z");

    @Mock
    private BalanceService balanceService;

    @Mock
    private BalanceoPlanner planner;

    @Mock
    private AccionBalanceoFactory fabrica;

    @Mock
    private EjecucionBalanceoRepository ejecuciones;

    @Mock
    private AccionBalanceoRepository acciones;

    @Mock
    private EventoDemandaRepository eventos;

    private BalanceoService servicio;
    private Usuario operador;
    private final CondicionesProduccion condiciones =
            CondicionesProduccion.de(null, null, null);

    @BeforeEach
    void preparar() {
        servicio = new BalanceoService(balanceService, planner, fabrica,
                new BalanceoInvoker(), ejecuciones, acciones, eventos,
                new AutorizacionService());

        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);
    }

    private ZonaRed zona(Long id, String nombre) {
        ZonaRed z = new ZonaRed();
        ReflectionTestUtils.setField(z, "id", id);
        z.setNombre(nombre);
        z.setTipo(TipoZona.SECTOR);
        return z;
    }

    private AnalisisBalance analisis() {
        ZonaAnalizada a = new ZonaAnalizada(
                zona(1L, "Sector A"), 150, 100, 0, 0, -50, List.of());
        ZonaAnalizada b = new ZonaAnalizada(
                zona(2L, "Sector B"), 40, 160, 0, 0, 120, List.of());

        return new AnalisisBalance(zona(10L, "Zona F4"),
                HASTA.minusSeconds(3600), HASTA, List.of(a, b));
    }

    private PlanCompleto planConTransferencia() {
        return new PlanCompleto("PROPORCIONAL",
                List.of(new TransferenciaPlan(2L, 1L, 50)),
                List.of(), List.of());
    }

    @Test
    void usuarioInactivoNoPuedeEjecutar() {
        Usuario inactivo = new Usuario("X", "x", "x", Rol.OPERADOR, false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.ejecutar(inactivo, 10L, "PROPORCIONAL",
                        HASTA, condiciones));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verifyNoInteractions(balanceService);
    }

    @Test
    void sinDesbalancesDevuelve409() {
        when(balanceService.analizar(eq(10L), any(), any()))
                .thenReturn(analisis());
        when(planner.planificar(any(), eq("PROPORCIONAL")))
                .thenReturn(new PlanCompleto("PROPORCIONAL",
                        List.of(), List.of(), List.of()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.ejecutar(operador, 10L, "PROPORCIONAL",
                        HASTA, condiciones));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(ejecuciones, never()).save(any());
    }

    @Test
    void ejecutarAplicaLosComandosDelPlan() {
        AccionBalanceoCommand comando = mock(AccionBalanceoCommand.class);

        when(balanceService.analizar(eq(10L), any(), any()))
                .thenReturn(analisis());
        when(planner.planificar(any(), eq("PROPORCIONAL")))
                .thenReturn(planConTransferencia());
        when(fabrica.comandos(any(), any())).thenReturn(List.of(comando));

        var respuesta = servicio.ejecutar(operador, 10L, "PROPORCIONAL",
                HASTA, condiciones);

        assertEquals("EJECUTADA", respuesta.estado());
        assertEquals("op", respuesta.ejecutadaPor());
        assertEquals(1, respuesta.acciones().size());
        verify(comando).ejecutar();
    }

    @Test
    void noSePuedeRevertirDosVeces() {
        EjecucionBalanceo ejecucion = new EjecucionBalanceo();
        ejecucion.setEstado(EstadoEjecucion.REVERTIDA);
        when(ejecuciones.findById(7L)).thenReturn(Optional.of(ejecucion));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.revertir(operador, 7L));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void revertirDeshaceLosComandosEnOrdenInverso() {
        EjecucionBalanceo ejecucion = new EjecucionBalanceo();
        ReflectionTestUtils.setField(ejecucion, "id", 7L);
        ejecucion.setZona(zona(10L, "Zona F4"));
        ejecucion.setEstado(EstadoEjecucion.EJECUTADA);
        ejecucion.setInstanteReferencia(HASTA);
        ejecucion.setIrradianciaWm2(new BigDecimal("1000"));
        ejecucion.setVientoMs(new BigDecimal("8"));
        ejecucion.setFactorDisponibilidad(new BigDecimal("0.2"));

        AccionBalanceoCommand primero = mock(AccionBalanceoCommand.class);
        AccionBalanceoCommand segundo = mock(AccionBalanceoCommand.class);

        when(ejecuciones.findById(7L)).thenReturn(Optional.of(ejecucion));
        when(acciones.findByEjecucionId(7L)).thenReturn(List.of());
        when(balanceService.analizar(eq(10L), any(), any()))
                .thenReturn(analisis());
        when(fabrica.comandos(any(), any()))
                .thenReturn(List.of(primero, segundo));

        var respuesta = servicio.revertir(operador, 7L);

        InOrder orden = inOrder(primero, segundo);
        orden.verify(segundo).deshacer();
        orden.verify(primero).deshacer();
        assertEquals("REVERTIDA", respuesta.estado());
        assertEquals("op", respuesta.revertidaPor());
    }
}