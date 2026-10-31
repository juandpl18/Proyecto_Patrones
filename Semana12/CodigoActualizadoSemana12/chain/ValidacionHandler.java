package com.smartgrid.smartgrid.chain;

public abstract class ValidacionHandler {

    protected ValidacionHandler siguiente;

    public ValidacionHandler establecerSiguiente(
            ValidacionHandler siguiente) {

        this.siguiente = siguiente;
        return siguiente;
    }

    public void validar(String nombre, String fuente, Double capacidad) {

        procesarValidacion(nombre, fuente, capacidad);

        if (siguiente != null) {
            siguiente.validar(nombre, fuente, capacidad);
        }
    }

    protected abstract void procesarValidacion(
            String nombre,
            String fuente,
            Double capacidad
    );
}