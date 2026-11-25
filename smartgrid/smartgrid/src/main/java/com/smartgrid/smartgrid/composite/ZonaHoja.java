package com.smartgrid.smartgrid.composite;

import java.math.BigDecimal;
import java.util.List;

/** Hoja: zona sin zonas hijas. Su consumo es solo el directo. */
public class ZonaHoja extends ZonaRedNodo {

    public ZonaHoja(
            Long id,
            String nombre,
            String tipo,
            BigDecimal capacidadKw,
            BigDecimal consumoDirecto) {

        super(id, nombre, tipo, capacidadKw, consumoDirecto);
    }

    @Override
    public BigDecimal getConsumoKwh() {
        return consumoDirecto;
    }

    @Override
    public List<ComponenteRed> getHijos() {
        return List.of();
    }
}