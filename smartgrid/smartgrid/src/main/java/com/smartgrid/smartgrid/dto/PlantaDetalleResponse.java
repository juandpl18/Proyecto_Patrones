package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.util.List;

public record PlantaDetalleResponse(
        Long id,
        String nombre,
        String tipoFuente,
        BigDecimal capacidadKw,
        BigDecimal voltajeSalida,
        boolean tieneBateriaReserva,
        String ubicacion,
        String estado,
        List<String> transicionesPermitidas,
        Long zonaId,
        String zonaNombre) {
}