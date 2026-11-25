package com.smartgrid.smartgrid.strategy;

import com.smartgrid.smartgrid.model.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class SolarProduccionStrategy implements ProduccionStrategy {

    private static final double EFICIENCIA_SISTEMA = 0.80;

    @Override
    public TipoFuente tipo() {
        return TipoFuente.SOLAR;
    }

    @Override
    public double calcularKw(double capacidadKw, CondicionesProduccion c) {
        double kw = capacidadKw * (c.irradianciaWm2() / 1000.0)
                * EFICIENCIA_SISTEMA;
        return Math.min(kw, capacidadKw);
    }

    @Override
    public String metodo() {
        return "Solar: capacidad x (irradiancia / 1000) x 0.80";
    }
}