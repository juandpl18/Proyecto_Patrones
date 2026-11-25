package com.smartgrid.smartgrid.strategy;

import com.smartgrid.smartgrid.model.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class HidraulicaProduccionStrategy implements ProduccionStrategy {

    @Override
    public TipoFuente tipo() {
        return TipoFuente.HIDRAULICA;
    }

    @Override
    public double calcularKw(double capacidadKw, CondicionesProduccion c) {
        return capacidadKw * c.factorDisponibilidad();
    }

    @Override
    public String metodo() {
        return "Hidráulica: capacidad x factor de disponibilidad";
    }
}