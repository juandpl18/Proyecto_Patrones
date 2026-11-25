package com.smartgrid.smartgrid.strategy.balanceo;

/** Entrada de una estrategia: positivo = superávit, negativo = déficit. */
public record ZonaBalance(Long zonaId, String nombre, double balanceKw) {
}