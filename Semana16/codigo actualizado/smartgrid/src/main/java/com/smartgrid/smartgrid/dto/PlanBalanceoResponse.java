package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.balanceo.AnalisisBalance;
import com.smartgrid.smartgrid.balanceo.PlanCompleto;
import com.smartgrid.smartgrid.balanceo.ZonaAnalizada;
import com.smartgrid.smartgrid.balanceo.Potencia;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record PlanBalanceoResponse(
        String estrategia,
        Long zonaId,
        String zonaNombre,
        Instant hasta,
        boolean requiereAccion,
        List<TransferenciaResponse> transferencias,
        List<DeslastreResponse> deslastres,
        List<String> observaciones) {

    public record TransferenciaResponse(
            Long origenId,
            String origenNombre,
            Long destinoId,
            String destinoNombre,
            BigDecimal kw) {
    }

    public record DeslastreResponse(
            Long zonaId,
            String zonaNombre,
            BigDecimal kw) {
    }

    public static PlanBalanceoResponse desde(
            PlanCompleto plan, AnalisisBalance analisis) {

        Map<Long, ZonaAnalizada> zonas = analisis.porId();

        List<TransferenciaResponse> transferencias = plan.transferencias()
                .stream()
                .map(t -> new TransferenciaResponse(
                        t.origenId(),
                        zonas.get(t.origenId()).zona().getNombre(),
                        t.destinoId(),
                        zonas.get(t.destinoId()).zona().getNombre(),
                        Potencia.kw(t.kw())))
                .toList();

        List<DeslastreResponse> deslastres = plan.deslastres().stream()
                .map(d -> new DeslastreResponse(
                        d.zonaId(),
                        zonas.get(d.zonaId()).zona().getNombre(),
                        Potencia.kw(d.kw())))
                .toList();

        return new PlanBalanceoResponse(
                plan.estrategia(),
                analisis.alcance().getId(),
                analisis.alcance().getNombre(),
                analisis.hasta(),
                !plan.estaVacio(),
                transferencias,
                deslastres,
                plan.observaciones());
    }
}