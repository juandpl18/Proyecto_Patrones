package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CrearTarifaRequest(
        String nombre,
        String tipoCliente,
        String modalidad,
        BigDecimal precioKwh,
        LocalDate vigenteDesde,
        List<TramoRequest> tramos,
        List<FranjaRequest> franjas) {

    public record TramoRequest(
            BigDecimal desdeKwh,
            BigDecimal hastaKwh,
            BigDecimal precioKwh) {
    }

    public record FranjaRequest(
            String nombre,
            Integer horaInicio,
            Integer horaFin,
            BigDecimal precioKwh) {
    }
}