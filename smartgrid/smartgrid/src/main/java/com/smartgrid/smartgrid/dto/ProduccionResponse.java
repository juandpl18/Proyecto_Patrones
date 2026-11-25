package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;

public record ProduccionResponse(
        Long plantaId,
        String nombre,
        String tipoFuente,
        String estado,
        boolean generando,
        BigDecimal capacidadKw,
        BigDecimal potencialKw,
        BigDecimal produccionKw,
        BigDecimal factorCapacidad,
        String metodo) {
}