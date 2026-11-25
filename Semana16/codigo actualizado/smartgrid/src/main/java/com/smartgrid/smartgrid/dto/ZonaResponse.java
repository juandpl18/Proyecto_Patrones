// ZonaResponse.java
package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.ZonaRed;

import java.math.BigDecimal;

public record ZonaResponse(
        Long id,
        String nombre,
        String tipo,
        Long zonaPadreId,
        BigDecimal capacidadKw) {

    public static ZonaResponse desde(ZonaRed z) {
        return new ZonaResponse(
                z.getId(),
                z.getNombre(),
                z.getTipo().name(),
                z.getZonaPadre() != null ? z.getZonaPadre().getId() : null,
                z.getCapacidadKw());
    }
}