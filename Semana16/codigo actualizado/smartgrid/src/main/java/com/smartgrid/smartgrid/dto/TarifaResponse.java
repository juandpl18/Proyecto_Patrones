package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.model.TarifaFranja;
import com.smartgrid.smartgrid.model.TarifaTramo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record TarifaResponse(
        Long id,
        String tipoCliente,
        String nombre,
        String modalidad,
        BigDecimal precioKwh,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta,
        boolean activa,
        List<TramoResponse> tramos,
        List<FranjaResponse> franjas) {

    public record TramoResponse(
            BigDecimal desdeKwh,
            BigDecimal hastaKwh,
            BigDecimal precioKwh) {
    }

    public record FranjaResponse(
            String nombre,
            int horaInicio,
            int horaFin,
            BigDecimal precioKwh) {
    }

    public static TarifaResponse desde(
            Tarifa t, List<TarifaTramo> tramos, List<TarifaFranja> franjas) {

        return new TarifaResponse(
                t.getId(),
                t.getTipoCliente().name(),
                t.getNombre(),
                t.getModalidad().name(),
                t.getPrecioKwh(),
                t.getVigenteDesde(),
                t.getVigenteHasta(),
                t.isActiva(),
                tramos.stream()
                        .map(x -> new TramoResponse(
                                x.getDesdeKwh(), x.getHastaKwh(),
                                x.getPrecioKwh()))
                        .toList(),
                franjas.stream()
                        .map(x -> new FranjaResponse(
                                x.getNombre(), x.getHoraInicio(),
                                x.getHoraFin(), x.getPrecioKwh()))
                        .toList());
    }
}