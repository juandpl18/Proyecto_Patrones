package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.LecturaHora;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.strategy.tarifa.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TarifaStrategyTest {

    private final PlanaTarifaStrategy plana = new PlanaTarifaStrategy();
    private final EscalonadaTarifaStrategy escalonada =
            new EscalonadaTarifaStrategy();
    private final HorariaTarifaStrategy horaria =
            new HorariaTarifaStrategy("America/Bogota");

    private DatosFacturacion consumo(String kwh) {
        return new DatosFacturacion(
                TipoCliente.RESIDENCIAL, new BigDecimal(kwh), List.of());
    }

    private void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " pero fue " + real);
    }

    private TarifaConfig escalonadaConfig() {
        return new TarifaConfig(ModalidadTarifa.ESCALONADA, "Esc", null,
                List.of(
                        new TarifaConfig.Tramo(new BigDecimal("0"),
                                new BigDecimal("130"), new BigDecimal("400")),
                        new TarifaConfig.Tramo(new BigDecimal("130"),
                                new BigDecimal("200"), new BigDecimal("600")),
                        new TarifaConfig.Tramo(new BigDecimal("200"),
                                null, new BigDecimal("800"))),
                List.of());
    }

    private TarifaConfig horariaConfig() {
        return new TarifaConfig(ModalidadTarifa.HORARIA, "Hor", null, List.of(),
                List.of(
                        new TarifaConfig.Franja("Valle", 22, 6,
                                new BigDecimal("300")),
                        new TarifaConfig.Franja("Normal", 6, 18,
                                new BigDecimal("500")),
                        new TarifaConfig.Franja("Pico", 18, 22,
                                new BigDecimal("900"))));
    }

    @Test
    void planaMultiplicaConsumoPorPrecio() {
        TarifaConfig config = new TarifaConfig(ModalidadTarifa.PLANA, "Base",
                new BigDecimal("500"), List.of(), List.of());

        assertValor("175000", plana.calcularEnergia(config, consumo("350")));
    }

    @Test
    void escalonadaCobraCadaTramoASuPrecio() {
        TarifaConfig config = escalonadaConfig();

        assertValor("40000", escalonada.calcularEnergia(config, consumo("100")));
        assertValor("52000", escalonada.calcularEnergia(config, consumo("130")));
        // 130x400 + 70x600 + 50x800
        assertValor("134000", escalonada.calcularEnergia(config, consumo("250")));
    }

    @Test
    void horariaCobraSegunLaHoraLocalDeCadaLectura() {
        // Bogotá es UTC-5: 10:30Z = 05:30 (valle), 15:00Z = 10:00 (normal),
        // 00:30Z del día siguiente = 19:30 (pico)
        DatosFacturacion datos = new DatosFacturacion(
                TipoCliente.RESIDENCIAL, new BigDecimal("30"),
                List.of(
                        new LecturaHora(Instant.parse("2026-11-21T10:30:00Z"),
                                BigDecimal.TEN),
                        new LecturaHora(Instant.parse("2026-11-21T15:00:00Z"),
                                BigDecimal.TEN),
                        new LecturaHora(Instant.parse("2026-11-22T00:30:00Z"),
                                BigDecimal.TEN)));

        // 10x300 + 10x500 + 10x900
        assertValor("17000", horaria.calcularEnergia(horariaConfig(), datos));
    }

    @Test
    void horariaSinLecturasSeRechaza() {
        assertThrows(IllegalArgumentException.class,
                () -> horaria.calcularEnergia(horariaConfig(), consumo("100")));
    }

    @Test
    void laFranjaQueCruzaLaMedianocheCubreLasHorasCorrectas() {
        TarifaConfig.Franja valle =
                new TarifaConfig.Franja("Valle", 22, 6, BigDecimal.ONE);

        assertTrue(valle.cubre(22));
        assertTrue(valle.cubre(23));
        assertTrue(valle.cubre(0));
        assertTrue(valle.cubre(5));
        assertFalse(valle.cubre(6));
        assertFalse(valle.cubre(12));
    }

    @Test
    void elRegistroEligeLaEstrategiaPorModalidad() {
        TarifaStrategyRegistry registro = new TarifaStrategyRegistry(
                List.of(plana, escalonada, horaria));

        assertSame(plana, registro.para(ModalidadTarifa.PLANA));
        assertSame(escalonada, registro.para(ModalidadTarifa.ESCALONADA));
        assertSame(horaria, registro.para(ModalidadTarifa.HORARIA));
    }
}