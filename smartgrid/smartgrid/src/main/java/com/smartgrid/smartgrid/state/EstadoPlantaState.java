package com.smartgrid.smartgrid.state;

import com.smartgrid.smartgrid.model.EstadoPlanta;

import java.util.Set;

/** Comportamiento de una planta según el estado en que se encuentra. */
public interface EstadoPlantaState {

    EstadoPlanta estado();

    /** ¿Una planta en este estado entrega energía a la red? */
    boolean generaEnergia();

    Set<EstadoPlanta> transicionesPermitidas();

    default boolean puedePasarA(EstadoPlanta destino) {
        return transicionesPermitidas().contains(destino);
    }
}