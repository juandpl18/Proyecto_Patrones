package com.smartgrid.smartgrid.facturacion;

import java.math.BigDecimal;

/** Una línea de la factura (energía o un cargo; puede ser negativa). */
public record LineaFactura(String concepto, BigDecimal valor) {
}