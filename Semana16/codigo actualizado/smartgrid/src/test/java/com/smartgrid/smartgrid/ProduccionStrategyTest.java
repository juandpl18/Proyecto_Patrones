package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.strategy.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProduccionStrategyTest {

    private final SolarProduccionStrategy solar = new SolarProduccionStrategy();
    private final EolicaProduccionStrategy eolica = new EolicaProduccionStrategy();
    private final HidraulicaProduccionStrategy hidraulica =
            new HidraulicaProduccionStrategy();

    private CondicionesProduccion irradiancia(double w) {
        return new CondicionesProduccion(w, 0, 0);
    }

    private CondicionesProduccion viento(double ms) {
        return new CondicionesProduccion(0, ms, 0);
    }

    @Test
    void solarConSolPlenoEntregaElOchentaPorCiento() {
        assertEquals(800.0, solar.calcularKw(1000, irradiancia(1000)), 0.001);
        assertEquals(400.0, solar.calcularKw(1000, irradiancia(500)), 0.001);
        assertEquals(0.0, solar.calcularKw(1000, irradiancia(0)), 0.001);
    }

    @Test
    void eolicaSigueLaCurvaDePotencia() {
        assertEquals(0.0, eolica.calcularKw(1000, viento(2)), 0.001);
        assertEquals(1000.0, eolica.calcularKw(1000, viento(12)), 0.001);
        assertEquals(1000.0, eolica.calcularKw(1000, viento(20)), 0.001);
        assertEquals(0.0, eolica.calcularKw(1000, viento(30)), 0.001);
        assertEquals(232.14, eolica.calcularKw(1000, viento(7.5)), 0.01);
    }

    @Test
    void hidraulicaDependeDeLaDisponibilidad() {
        assertEquals(350.0,
                hidraulica.calcularKw(500,
                        new CondicionesProduccion(0, 0, 0.7)), 0.001);
    }

    @Test
    void condicionesFueraDeRangoSeRechazan() {
        assertThrows(IllegalArgumentException.class,
                () -> CondicionesProduccion.de(5000.0, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> CondicionesProduccion.de(null, -1.0, null));
        assertThrows(IllegalArgumentException.class,
                () -> CondicionesProduccion.de(null, null, 1.5));
    }

    @Test
    void sinParametrosUsaValoresPorDefecto() {
        CondicionesProduccion c = CondicionesProduccion.de(null, null, null);
        assertEquals(800.0, c.irradianciaWm2());
        assertEquals(8.0, c.vientoMs());
        assertEquals(0.70, c.factorDisponibilidad());
    }
}