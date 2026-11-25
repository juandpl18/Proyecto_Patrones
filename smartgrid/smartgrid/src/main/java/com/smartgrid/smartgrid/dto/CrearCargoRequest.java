package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;

public record CrearCargoRequest(
        String codigo,
        String nombre,
        String tipoCliente,
        String tipoCalculo,
        BigDecimal valor) {
}