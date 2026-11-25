package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.dto.CrearTarifaRequest;
import com.smartgrid.smartgrid.dto.CrearTarifaRequest.FranjaRequest;
import com.smartgrid.smartgrid.dto.CrearTarifaRequest.TramoRequest;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.TarifaFranjaRepository;
import com.smartgrid.smartgrid.repository.TarifaRepository;
import com.smartgrid.smartgrid.repository.TarifaTramoRepository;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.TarifaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarifaServiceTest {

    @Mock
    private TarifaRepository tarifas;

    @Mock
    private TarifaTramoRepository tramos;

    @Mock
    private TarifaFranjaRepository franjas;

    private TarifaService servicio;
    private Usuario admin;
    private Usuario operador;

    @BeforeEach
    void preparar() {
        servicio = new TarifaService(tarifas, tramos, franjas,
                new AutorizacionService());

        admin = new Usuario("Admin", "admin", "x", Rol.ADMIN, true);
        operador = new Usuario("Op", "op", "x", Rol.OPERADOR, true);
    }

    private TramoRequest tramo(String desde, String hasta, String precio) {
        return new TramoRequest(new BigDecimal(desde),
                hasta == null ? null : new BigDecimal(hasta),
                new BigDecimal(precio));
    }

    private CrearTarifaRequest escalonada(
            LocalDate desde, List<TramoRequest> tramos) {

        return new CrearTarifaRequest("Escalonada", "RESIDENCIAL",
                "ESCALONADA", null, desde, tramos, null);
    }

    private CrearTarifaRequest escalonadaValida(LocalDate desde) {
        return escalonada(desde, List.of(
                tramo("0", "100", "400"),
                tramo("100", "200", "600"),
                tramo("200", null, "800")));
    }

    private void assertEstado(HttpStatus esperado, Runnable accion) {
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class, accion::run);
        assertEquals(esperado, ex.getStatusCode());
    }

    @Test
    void creaUnaTarifaEscalonadaYCierraLaVigenteAnterior() {
        Tarifa vigente = new Tarifa();
        vigente.setTipoCliente(TipoCliente.RESIDENCIAL);
        vigente.setVigenteDesde(LocalDate.of(2026, 1, 1));

        when(tarifas.findByTipoClienteAndActivaTrueOrderByVigenteDesdeDesc(
                TipoCliente.RESIDENCIAL)).thenReturn(List.of(vigente));
        when(tarifas.save(any(Tarifa.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var respuesta = servicio.crear(admin,
                escalonadaValida(LocalDate.of(2026, 11, 1)));

        assertEquals("ESCALONADA", respuesta.modalidad());
        assertEquals(3, respuesta.tramos().size());
        assertEquals(LocalDate.of(2026, 10, 31), vigente.getVigenteHasta());
    }

    @Test
    void rechazaTramosConHueco() {
        assertEstado(HttpStatus.BAD_REQUEST, () -> servicio.crear(admin,
                escalonada(LocalDate.of(2026, 11, 1), List.of(
                        tramo("0", "100", "400"),
                        tramo("150", null, "600")))));

        verify(tarifas, never()).save(any());
    }

    @Test
    void rechazaTarifaEscalonadaSinTramoAbierto() {
        assertEstado(HttpStatus.BAD_REQUEST, () -> servicio.crear(admin,
                escalonada(LocalDate.of(2026, 11, 1), List.of(
                        tramo("0", "100", "400"),
                        tramo("100", "200", "600")))));
    }

    @Test
    void rechazaFranjasQueNoCubrenLas24Horas() {
        CrearTarifaRequest incompleta = new CrearTarifaRequest("Horaria",
                "RESIDENCIAL", "HORARIA", null, LocalDate.of(2026, 11, 1),
                null, List.of(new FranjaRequest(
                "Normal", 6, 18, new BigDecimal("500"))));

        assertEstado(HttpStatus.BAD_REQUEST,
                () -> servicio.crear(admin, incompleta));
    }

    @Test
    void rechazaTarifaPlanaSinPrecio() {
        CrearTarifaRequest sinPrecio = new CrearTarifaRequest("Plana",
                "COMERCIAL", "PLANA", null, LocalDate.of(2026, 11, 1),
                null, null);

        assertEstado(HttpStatus.BAD_REQUEST,
                () -> servicio.crear(admin, sinPrecio));
    }

    @Test
    void laNuevaTarifaDebeIniciarDespuesDeLaVigente() {
        Tarifa vigente = new Tarifa();
        vigente.setTipoCliente(TipoCliente.RESIDENCIAL);
        vigente.setVigenteDesde(LocalDate.of(2026, 11, 1));

        when(tarifas.findByTipoClienteAndActivaTrueOrderByVigenteDesdeDesc(
                TipoCliente.RESIDENCIAL)).thenReturn(List.of(vigente));

        assertEstado(HttpStatus.CONFLICT, () -> servicio.crear(admin,
                escalonadaValida(LocalDate.of(2026, 11, 1))));

        verify(tarifas, never()).save(any());
    }

    @Test
    void soloElAdminCreaTarifas() {
        assertEstado(HttpStatus.FORBIDDEN, () -> servicio.crear(operador,
                escalonadaValida(LocalDate.of(2026, 11, 1))));
    }
}