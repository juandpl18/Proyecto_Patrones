package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.facturacion.ConstructorCalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.model.*;
import com.smartgrid.smartgrid.repository.CargoAdicionalRepository;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.service.TarifaService;
import com.smartgrid.smartgrid.strategy.tarifa.*;
import com.smartgrid.smartgrid.template.FacturaCalculada;
import com.smartgrid.smartgrid.template.PlantillaFacturacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlantillaFacturacionTest {

    private static final LocalDate DESDE = LocalDate.of(2026, 11, 1);
    private static final LocalDate HASTA = LocalDate.of(2026, 11, 22);

    @Mock
    private TarifaService tarifaService;

    @Mock
    private CargoAdicionalRepository cargos;

    @Mock
    private FacturaEmitidaRepository facturas;

    @Mock
    private FacturaDetalleRepository detalles;

    private PlantillaFacturacion plantilla;
    private Cliente cliente;
    private Tarifa tarifa;

    @BeforeEach
    void preparar() {
        TarifaStrategyRegistry registro = new TarifaStrategyRegistry(List.of(
                new PlanaTarifaStrategy(),
                new EscalonadaTarifaStrategy(),
                new HorariaTarifaStrategy("America/Bogota")));

        ConstructorCalculoFactura constructor =
                new ConstructorCalculoFactura(registro, cargos);

        // Subclase mínima: solo define de dónde sale el consumo (350 kWh)
        plantilla = new PlantillaFacturacion(tarifaService, constructor,
                facturas, detalles, ZoneId.of("America/Bogota")) {

            @Override
            public ModoFacturacion modo() {
                return ModoFacturacion.LECTURAS;
            }

            @Override
            protected DatosFacturacion obtenerConsumo(
                    Cliente c, LocalDate desde, LocalDate hasta) {

                return new DatosFacturacion(TipoCliente.RESIDENCIAL,
                        new BigDecimal("350"), List.of());
            }
        };

        cliente = new Cliente();
        cliente.setNombre("Cliente");
        cliente.setTipo(TipoCliente.RESIDENCIAL);
        cliente.setActivo(true);

        tarifa = new Tarifa();
        tarifa.setNombre("Base");
        tarifa.setTipoCliente(TipoCliente.RESIDENCIAL);
        tarifa.setModalidad(ModalidadTarifa.PLANA);
        tarifa.setPrecioKwh(new BigDecimal("500"));
    }

    private void tarifaVigente() {
        when(tarifaService.vigente(eq(TipoCliente.RESIDENCIAL), any()))
                .thenReturn(tarifa);
        when(tarifaService.configurar(tarifa)).thenReturn(new TarifaConfig(
                ModalidadTarifa.PLANA, "Base", new BigDecimal("500"),
                List.of(), List.of()));
    }

    @Test
    void simularCalculaPeroNoGuardaNada() {
        tarifaVigente();

        FacturaCalculada r =
                plantilla.ejecutar(cliente, DESDE, HASTA, null, "admin", false);

        assertEquals("SIMULADA", r.estado());
        assertEquals(0, new BigDecimal("175000").compareTo(r.resultado().total()));
        verify(facturas, never()).save(any());
        verifyNoInteractions(detalles);
    }

    @Test
    void emitirGuardaLaFacturaYSusLineas() {
        tarifaVigente();
        when(facturas.existsByClienteIdAndPeriodoInicioAndPeriodoFinAndEstadoNot(
                any(), any(), any(), any())).thenReturn(false);
        when(facturas.save(any(FacturaEmitida.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        FacturaCalculada r =
                plantilla.ejecutar(cliente, DESDE, HASTA, null, "admin", true);

        assertEquals("EMITIDA", r.estado());

        ArgumentCaptor<FacturaEmitida> captor =
                ArgumentCaptor.forClass(FacturaEmitida.class);
        verify(facturas).save(captor.capture());
        assertEquals(EstadoFactura.EMITIDA, captor.getValue().getEstado());
        assertEquals("admin", captor.getValue().getEmitidaPor());
        assertEquals(0, new BigDecimal("500")
                .compareTo(captor.getValue().getPrecioKwh()));

        verify(detalles).saveAll(any());
    }

    @Test
    void noEmiteDosVecesElMismoPeriodo() {
        tarifaVigente();
        when(facturas.existsByClienteIdAndPeriodoInicioAndPeriodoFinAndEstadoNot(
                any(), any(), any(), any())).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> plantilla.ejecutar(
                        cliente, DESDE, HASTA, null, "admin", true));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(facturas, never()).save(any());
    }

    @Test
    void unClienteInactivoNoSeFactura() {
        cliente.setActivo(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> plantilla.ejecutar(
                        cliente, DESDE, HASTA, null, "admin", true));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void unPeriodoFuturoSeRechaza() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> plantilla.ejecutar(cliente, DESDE,
                        LocalDate.now().plusDays(10), null, "admin", false));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}