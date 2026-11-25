package com.smartgrid.smartgrid.facturacion;

import java.math.BigDecimal;
import java.time.Instant;

public record LecturaHora(Instant fechaHora, BigDecimal kwh) {
}