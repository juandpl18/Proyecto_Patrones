package com.smartgrid.smartgrid.composite;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Compuesto: su consumo es el propio más el de todos sus hijos. */
public class ZonaCompuesta extends ZonaRedNodo {

    private final List<ComponenteRed> hijos = new ArrayList<>();

    public ZonaCompuesta(
            Long id,
            String nombre,
            String tipo,
            BigDecimal capacidadKw,
            BigDecimal consumoDirecto) {

        super(id, nombre, tipo, capacidadKw, consumoDirecto);
    }

    public void agregar(ComponenteRed hijo) {
        hijos.add(hijo);
    }

    @Override
    public BigDecimal getConsumoKwh() {
        return hijos.stream()
                .map(ComponenteRed::getConsumoKwh)
                .reduce(consumoDirecto, BigDecimal::add);
    }

    @Override
    public List<ComponenteRed> getHijos() {
        return Collections.unmodifiableList(hijos);
    }
}