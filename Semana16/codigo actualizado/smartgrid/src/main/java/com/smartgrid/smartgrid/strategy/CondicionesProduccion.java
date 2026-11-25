package com.smartgrid.smartgrid.strategy;

/** Condiciones ambientales con las que se simula la producción. */
public record CondicionesProduccion(
        double irradianciaWm2,
        double vientoMs,
        double factorDisponibilidad) {

    /** Valores por defecto de simulación: 800 W/m², 8 m/s y 70 %. */
    public static CondicionesProduccion de(
            Double irradianciaWm2, Double vientoMs, Double factorDisponibilidad) {

        double irradiancia = irradianciaWm2 != null ? irradianciaWm2 : 800.0;
        double viento = vientoMs != null ? vientoMs : 8.0;
        double disponibilidad =
                factorDisponibilidad != null ? factorDisponibilidad : 0.70;

        if (!(irradiancia >= 0 && irradiancia <= 1500)) {
            throw new IllegalArgumentException(
                    "irradianciaWm2 debe estar entre 0 y 1500");
        }
        if (!(viento >= 0 && viento <= 60)) {
            throw new IllegalArgumentException(
                    "vientoMs debe estar entre 0 y 60");
        }
        if (!(disponibilidad >= 0 && disponibilidad <= 1)) {
            throw new IllegalArgumentException(
                    "factorDisponibilidad debe estar entre 0 y 1");
        }

        return new CondicionesProduccion(irradiancia, viento, disponibilidad);
    }
}