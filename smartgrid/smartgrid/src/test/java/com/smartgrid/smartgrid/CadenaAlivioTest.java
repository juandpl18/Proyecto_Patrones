package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.chain.alivio.AlivioContexto;
import com.smartgrid.smartgrid.chain.alivio.CadenaAlivio;
import com.smartgrid.smartgrid.chain.alivio.PlantaRecuperable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CadenaAlivioTest {

    private final CadenaAlivio cadena = new CadenaAlivio();

    @Test
    void undeficitPequenoSeToleraYNoSeDeslastra() {
        AlivioContexto ctx = cadena.evaluar(1L, "Sector A", 1.0, 100.0, List.of());

        assertEquals(0.0, ctx.getDeslastreKw(), 0.001);
        assertEquals(0.0, ctx.getDeficitPendiente(), 0.001);
        assertEquals(1, ctx.getObservaciones().size());
    }

    @Test
    void unDeficitGrandeSeDeslastraCompleto() {
        AlivioContexto ctx = cadena.evaluar(1L, "Sector A", 30.0, 100.0, List.of());

        assertEquals(30.0, ctx.getDeslastreKw(), 0.001);
        assertEquals(0.0, ctx.getDeficitPendiente(), 0.001);
    }

    @Test
    void recomiendaActivarPlantasEnMantenimientoAntesDeDeslastrar() {
        AlivioContexto ctx = cadena.evaluar(1L, "Sector A", 30.0, 100.0,
                List.of(new PlantaRecuperable("Hidro", 100.0)));

        assertEquals(30.0, ctx.getDeslastreKw(), 0.001);
        assertEquals(2, ctx.getObservaciones().size());
        assertTrue(ctx.getObservaciones().get(0).contains("Hidro"));
    }
}