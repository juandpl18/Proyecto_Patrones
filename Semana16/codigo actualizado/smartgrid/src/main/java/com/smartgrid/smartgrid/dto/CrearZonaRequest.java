package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;

public record CrearZonaRequest(
        String nombre,
        String tipo,
        Long zonaPadreId,
        BigDecimal capacidadKw) {
}