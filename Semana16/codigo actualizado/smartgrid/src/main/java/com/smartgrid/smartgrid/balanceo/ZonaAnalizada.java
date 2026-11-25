package com.smartgrid.smartgrid.balanceo;

import com.smartgrid.smartgrid.chain.alivio.PlantaRecuperable;
import com.smartgrid.smartgrid.model.ZonaRed;

import java.util.List;

/** Situación de una zona hija: demanda, generación y ajustes vigentes. */
public record ZonaAnalizada(
        ZonaRed zona,
        double demandaKw,
        double generacionKw,
        double transferenciasNetasKw,
        double deslastreKw,
        double balanceKw,
        List<PlantaRecuperable> plantasRecuperables) {

    public double demandaEfectivaKw() {
        return demandaKw - deslastreKw;
    }
}