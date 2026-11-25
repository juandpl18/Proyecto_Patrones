package com.smartgrid.smartgrid.strategy;

import com.smartgrid.smartgrid.model.TipoFuente;

/** Algoritmo de producción de una fuente de energía. */
public interface ProduccionStrategy {

    TipoFuente tipo();

    /** Potencia en kW que entregaría la planta con esas condiciones. */
    double calcularKw(double capacidadKw, CondicionesProduccion condiciones);

    /** Descripción de la fórmula, para mostrarla en la respuesta. */
    String metodo();
}