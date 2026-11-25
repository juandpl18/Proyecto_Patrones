package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RegistrarLecturaRequest(
        String codigoMedidor,
        Instant fechaHora,
        BigDecimal consumoKwh) {
}