package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;

public record ZonaBalanceResponse(
        Long zonaId,
        String nombre,
        String tipo,
        BigDecimal demandaKw,
        BigDecimal generacionKw,
        BigDecimal transferenciasNetasKw,
        BigDecimal deslastreKw,
        BigDecimal balanceKw,
        String estado) {
}