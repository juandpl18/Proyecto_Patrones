package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.strategy.CondicionesProduccion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record GeneracionResumenResponse(
        long totalPlantas,
        BigDecimal capacidadInstaladaKw,
        BigDecimal capacidadDisponibleKw,
        BigDecimal produccionEstimadaKw,
        Map<String, Long> plantasPorEstado,
        List<FuenteResumen> porFuente,
        CondicionesProduccion condiciones) {

    public record FuenteResumen(
            String tipoFuente,
            long plantas,
            BigDecimal capacidadKw,
            BigDecimal produccionEstimadaKw) {
    }
}