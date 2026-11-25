package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.model.ModalidadTarifa;

import java.math.BigDecimal;
import java.util.List;

/** Parámetros de una tarifa ya cargados de la BD (el cálculo no toca JPA). */
public record TarifaConfig(
        ModalidadTarifa modalidad,
        String nombre,
        BigDecimal precioKwh,
        List<Tramo> tramos,
        List<Franja> franjas) {

    /** Tramo de consumo [desde, hasta). Hasta nulo = sin límite. */
    public record Tramo(
            BigDecimal desdeKwh,
            BigDecimal hastaKwh,
            BigDecimal precioKwh) {
    }

    /** Franja horaria [horaInicio, horaFin). Si fin < inicio, cruza la medianoche. */
    public record Franja(
            String nombre,
            int horaInicio,
            int horaFin,
            BigDecimal precioKwh) {

        public boolean cubre(int hora) {
            if (horaInicio < horaFin) {
                return hora >= horaInicio && hora < horaFin;
            }
            return hora >= horaInicio || hora < horaFin;
        }
    }
}