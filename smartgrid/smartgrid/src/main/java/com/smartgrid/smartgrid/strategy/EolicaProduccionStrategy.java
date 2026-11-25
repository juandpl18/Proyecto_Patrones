package com.smartgrid.smartgrid.strategy;

import com.smartgrid.smartgrid.model.TipoFuente;
import org.springframework.stereotype.Component;

@Component
public class EolicaProduccionStrategy implements ProduccionStrategy {

    private static final double VELOCIDAD_ARRANQUE = 3.0;
    private static final double VELOCIDAD_NOMINAL = 12.0;
    private static final double VELOCIDAD_CORTE = 25.0;

    @Override
    public TipoFuente tipo() {
        return TipoFuente.EOLICA;
    }

    @Override
    public double calcularKw(double capacidadKw, CondicionesProduccion c) {
        double v = c.vientoMs();

        if (v < VELOCIDAD_ARRANQUE || v > VELOCIDAD_CORTE) {
            return 0.0;
        }
        if (v >= VELOCIDAD_NOMINAL) {
            return capacidadKw;
        }

        double arranque = Math.pow(VELOCIDAD_ARRANQUE, 3);
        double fraccion = (Math.pow(v, 3) - arranque)
                / (Math.pow(VELOCIDAD_NOMINAL, 3) - arranque);

        return capacidadKw * fraccion;
    }

    @Override
    public String metodo() {
        return "Eólica: 0 bajo 3 m/s o sobre 25 m/s; entre 3 y 12 m/s "
                + "crece con el cubo de la velocidad; desde 12 m/s, nominal";
    }
}