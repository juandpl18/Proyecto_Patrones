package com.smartgrid.smartgrid.command.balanceo;

import com.smartgrid.smartgrid.model.ZonaRed;

/** Demanda y generación de una zona, para auditar en evento_demanda. */
public record SnapshotZona(
        ZonaRed zona,
        double demandaKw,
        double generacionKw) {
}