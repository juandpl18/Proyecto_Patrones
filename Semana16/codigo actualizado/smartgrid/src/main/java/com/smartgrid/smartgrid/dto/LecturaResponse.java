package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.LecturaConsumo;

import java.math.BigDecimal;
import java.time.Instant;

public record LecturaResponse(
        Long id,
        String codigoMedidor,
        Instant fechaHora,
        BigDecimal consumoKwh) {

    public static LecturaResponse desde(LecturaConsumo l) {
        return new LecturaResponse(
                l.getId(),
                l.getMedidor().getCodigo(),
                l.getFechaHora(),
                l.getConsumoKwh());
    }
}