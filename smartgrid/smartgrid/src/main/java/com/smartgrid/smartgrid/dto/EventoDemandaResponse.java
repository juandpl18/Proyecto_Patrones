package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.EventoDemanda;

import java.math.BigDecimal;
import java.time.Instant;

public record EventoDemandaResponse(
        Long id,
        Long zonaId,
        String zonaNombre,
        Instant fechaHora,
        BigDecimal demandaKw,
        BigDecimal generacionKw,
        String accion) {

    public static EventoDemandaResponse desde(EventoDemanda e) {
        return new EventoDemandaResponse(
                e.getId(),
                e.getZona().getId(),
                e.getZona().getNombre(),
                e.getFechaHora(),
                e.getDemandaKw(),
                e.getGeneracionKw(),
                e.getAccion());
    }
}