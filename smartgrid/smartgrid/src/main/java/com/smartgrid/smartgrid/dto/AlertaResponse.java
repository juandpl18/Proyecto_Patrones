package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Alerta;

import java.time.Instant;

public record AlertaResponse(
        Long id,
        Long zonaId,
        String zonaNombre,
        String tipo,
        String severidad,
        String mensaje,
        Instant creadaEn,
        boolean atendida) {

    public static AlertaResponse desde(Alerta a) {
        return new AlertaResponse(
                a.getId(),
                a.getZona() != null ? a.getZona().getId() : null,
                a.getZona() != null ? a.getZona().getNombre() : null,
                a.getTipo().name(),
                a.getSeveridad().name(),
                a.getMensaje(),
                a.getCreadaEn(),
                a.isAtendida());
    }
}