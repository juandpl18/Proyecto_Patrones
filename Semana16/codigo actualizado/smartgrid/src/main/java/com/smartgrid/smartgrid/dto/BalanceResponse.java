package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BalanceResponse(
        Long zonaId,
        String zonaNombre,
        Instant desde,
        Instant hasta,
        BigDecimal totalDemandaKw,
        BigDecimal totalGeneracionKw,
        BigDecimal balanceTotalKw,
        String estado,
        List<ZonaBalanceResponse> zonas) {
}