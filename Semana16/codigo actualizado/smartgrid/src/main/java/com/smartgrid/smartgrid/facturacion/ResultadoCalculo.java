package com.smartgrid.smartgrid.facturacion;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoCalculo(
        BigDecimal energia,
        List<LineaFactura> lineas) {

    public BigDecimal total() {
        return lineas.stream()
                .map(LineaFactura::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}