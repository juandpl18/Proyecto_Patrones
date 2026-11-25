package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.PlantaGeneracion;

import java.math.BigDecimal;
import java.time.Instant;

public record PlantaResponse(
        Long id,
        String nombre,
        String tipoFuente,
        BigDecimal capacidadKw,
        BigDecimal voltajeSalida,
        boolean tieneBateriaReserva,
        String ubicacion,
        String estado,
        String creadoPor,
        Instant creadoEn) {

    public static PlantaResponse desde(PlantaGeneracion p) {
        return new PlantaResponse(
                p.getId(),
                p.getNombre(),
                p.getTipoFuente().name(),
                p.getCapacidadKw(),
                p.getVoltajeSalida(),
                p.isTieneBateriaReserva(),
                p.getUbicacion(),
                p.getEstado().name(),
                p.getCreadoPor(),
                p.getCreadoEn());
    }
}