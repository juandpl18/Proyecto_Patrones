package com.smartgrid.smartgrid.strategy.balanceo;

import java.util.List;
import java.util.Map;

/** Salida de una estrategia: transferencias y déficit que no se pudo cubrir. */
public record PlanBalanceo(
        List<TransferenciaPlan> transferencias,
        Map<Long, Double> deficitResidual) {
}