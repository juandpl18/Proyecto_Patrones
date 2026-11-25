package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.CambioEstadoPlanta;

import java.time.Instant;

public record CambioEstadoResponse(
        Long id,
        Long plantaId,
        String estadoAnterior,
        String estadoNuevo,
        String motivo,
        String usuario,
        Instant fechaHora) {

    public static CambioEstadoResponse desde(CambioEstadoPlanta c) {
        return new CambioEstadoResponse(
                c.getId(),
                c.getPlanta().getId(),
                c.getEstadoAnterior().name(),
                c.getEstadoNuevo().name(),
                c.getMotivo(),
                c.getUsuario(),
                c.getFechaHora());
    }
}