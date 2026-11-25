package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.model.*;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.FacturaService;
import com.smartgrid.smartgrid.service.TarifaService;
import com.smartgrid.smartgrid.template.PlantillaFacturacionRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturaServiceTest {

    @Mock
    private FacturaEmitidaRepository facturas;

    @Mock
    private FacturaDetalleRepository detalles;

    @Mock
    private ClienteRepository clientes;

    @Mock
    private TarifaService tarifaService;

    @Mock
    private PlantillaFacturacionRegistry plantillas;

    private FacturaService servicio;
    private Usuario admin;
    private Usuario operador;

    @BeforeEach
    void preparar() {
        servicio = new FacturaService(facturas, detalles, clientes,
                tarifaService, plantillas, new AutorizacionService(),
                "America/Bogota");

        admin = new Usuario("Admin", "admin", "x", Rol.ADMIN, true);
        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);
    }

    private FacturaEmitida factura(EstadoFactura estado) {
        Cliente cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "id", 3L);
        cliente.setNombre("Cliente");
        cliente.setTipo(TipoCliente.RESIDENCIAL);

        Tarifa tarifa = new Tarifa();
        ReflectionTestUtils.setField(tarifa, "id", 1L);
        tarifa.setNombre("Base");
        tarifa.setModalidad(ModalidadTarifa.PLANA);

        FacturaEmitida f = new FacturaEmitida();
        ReflectionTestUtils.setField(f, "id", 9L);
        f.setCliente(cliente);
        f.setTarifa(tarifa);
        f.setPeriodoInicio(LocalDate.of(2026, 11, 1));
        f.setPeriodoFin(LocalDate.of(2026, 11, 22));
        f.setConsumoKwh(BigDecimal.TEN);
        f.setPrecioKwh(new BigDecimal("500"));
        f.setEnergia(new BigDecimal("5000"));
        f.setTotal(new BigDecimal("5000"));
        f.setEstado(estado);
        f.setModo(ModoFacturacion.LECTURAS);

        when(facturas.findById(9L)).thenReturn(Optional.of(f));
        return f;
    }

    private void assertEstado(HttpStatus esperado, Runnable accion) {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, accion::run);
        assertEquals(esperado, ex.getStatusCode());
    }

    @Test
    void unaFacturaEmitidaSePuedePagar() {
        FacturaEmitida f = factura(EstadoFactura.EMITIDA);

        var respuesta = servicio.pagar(operador, 9L);

        assertEquals("PAGADA", respuesta.estado());
        assertNotNull(f.getPagadaEn());
    }

    @Test
    void unaFacturaAnuladaNoSePuedePagar() {
        factura(EstadoFactura.ANULADA);

        assertEstado(HttpStatus.CONFLICT, () -> servicio.pagar(operador, 9L));
    }

    @Test
    void unaFacturaPagadaNoSePuedeAnular() {
        factura(EstadoFactura.PAGADA);

        assertEstado(HttpStatus.CONFLICT,
                () -> servicio.anular(admin, 9L, "Error de digitación"));
    }

    @Test
    void anularExigeMotivo() {
        assertEstado(HttpStatus.BAD_REQUEST,
                () -> servicio.anular(admin, 9L, "  "));

        verify(facturas, never()).save(any());
    }

    @Test
    void soloElAdminAnula() {
        assertEstado(HttpStatus.FORBIDDEN,
                () -> servicio.anular(operador, 9L, "Motivo"));
    }

    @Test
    void anulaConMotivoYQuedaRegistrado() {
        FacturaEmitida f = factura(EstadoFactura.VENCIDA);

        var respuesta = servicio.anular(admin, 9L, "Cliente duplicado");

        assertEquals("ANULADA", respuesta.estado());
        assertEquals("Cliente duplicado", f.getMotivoAnulacion());
        assertNotNull(f.getAnuladaEn());
    }
}