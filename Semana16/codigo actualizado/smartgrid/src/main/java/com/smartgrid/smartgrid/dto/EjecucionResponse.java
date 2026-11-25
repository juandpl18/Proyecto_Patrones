package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.AccionBalanceo;
import com.smartgrid.smartgrid.model.EjecucionBalanceo;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record EjecucionResponse(
        Long id,
        Long zonaId,
        String zonaNombre,
        String estrategia,
        String estado,
        Instant instanteReferencia,
        String ejecutadaPor,
        Instant ejecutadaEn,
        String revertidaPor,
        Instant revertidaEn,
        List<AccionResponse> acciones) {

    public record AccionResponse(
            String tipo,
            Long zonaOrigenId,
            String zonaOrigenNombre,
            Long zonaId,
            String zonaNombre,
            BigDecimal kw) {

        public static AccionResponse desde(AccionBalanceo a) {
            return new AccionResponse(
                    a.getTipo().name(),
                    a.getZonaOrigen() != null ? a.getZonaOrigen().getId() : null,
                    a.getZonaOrigen() != null
                            ? a.getZonaOrigen().getNombre() : null,
                    a.getZona().getId(),
                    a.getZona().getNombre(),
                    a.getKw());
        }
    }

    public static EjecucionResponse desde(
            EjecucionBalanceo e, List<AccionBalanceo> acciones) {

        return new EjecucionResponse(
                e.getId(),
                e.getZona().getId(),
                e.getZona().getNombre(),
                e.getEstrategia(),
                e.getEstado().name(),
                e.getInstanteReferencia(),
                e.getEjecutadaPor(),
                e.getEjecutadaEn(),
                e.getRevertidaPor(),
                e.getRevertidaEn(),
                acciones.stream().map(AccionResponse::desde).toList());
    }
}