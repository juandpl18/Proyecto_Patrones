package com.smartgrid.smartgrid.chain.alivio;

import java.util.Locale;

/** Recomienda reactivar plantas en mantenimiento (no cambia el déficit). */
public class PlantasMantenimientoRegla extends ReglaAlivio {

    @Override
    protected void manejar(AlivioContexto contexto) {
        for (PlantaRecuperable planta : contexto.getPlantasRecuperables()) {
            contexto.recomendar(String.format(Locale.ROOT,
                    "activar la planta '%s' (en mantenimiento, aportaría "
                            + "hasta %.3f kW) podría cubrir el déficit",
                    planta.nombre(), planta.potencialKw()));
        }
    }
}