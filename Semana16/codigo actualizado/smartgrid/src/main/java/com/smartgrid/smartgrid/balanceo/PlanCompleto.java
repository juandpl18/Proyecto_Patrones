package com.smartgrid.smartgrid.balanceo;

import com.smartgrid.smartgrid.strategy.balanceo.TransferenciaPlan;

import java.util.List;

/** Plan final: transferencias, deslastres y observaciones de la cadena. */
public record PlanCompleto(
        String estrategia,
        List<TransferenciaPlan> transferencias,
        List<DeslastrePlan> deslastres,
        List<String> observaciones) {

    public boolean estaVacio() {
        return transferencias.isEmpty() && deslastres.isEmpty();
    }
}