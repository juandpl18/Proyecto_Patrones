package com.smartgrid.smartgrid.chain.alivio;

/** Planta en mantenimiento que podría aportar si se reactivara. */
public record PlantaRecuperable(String nombre, double potencialKw) {
}