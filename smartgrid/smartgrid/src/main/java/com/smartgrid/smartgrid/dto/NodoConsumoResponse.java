package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.composite.ComponenteRed;

import java.math.BigDecimal;
import java.util.List;

public record NodoConsumoResponse(
        Long id,
        String nombre,
        String tipo,
        BigDecimal capacidadKw,
        BigDecimal consumoKwh,
        List<NodoConsumoResponse> hijos) {

    public static NodoConsumoResponse desde(ComponenteRed c) {
        return new NodoConsumoResponse(
                c.getId(),
                c.getNombre(),
                c.getTipo(),
                c.getCapacidadKw(),
                c.getConsumoKwh(),
                c.getHijos().stream()
                        .map(NodoConsumoResponse::desde)
                        .toList());
    }
}