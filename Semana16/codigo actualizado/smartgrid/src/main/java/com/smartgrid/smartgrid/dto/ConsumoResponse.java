package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ConsumoResponse(
        String tipo,
        Long id,
        String nombre,
        Instant desde,
        Instant hasta,
        BigDecimal consumoKwh) {
}