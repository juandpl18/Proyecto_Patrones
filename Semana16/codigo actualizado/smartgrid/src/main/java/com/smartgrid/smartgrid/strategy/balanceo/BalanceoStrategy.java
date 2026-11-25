package com.smartgrid.smartgrid.strategy.balanceo;

import java.util.List;

/** Algoritmo que reparte el superávit entre las zonas en déficit. */
public interface BalanceoStrategy {

    String nombre();

    String descripcion();

    PlanBalanceo planificar(List<ZonaBalance> zonas);
}