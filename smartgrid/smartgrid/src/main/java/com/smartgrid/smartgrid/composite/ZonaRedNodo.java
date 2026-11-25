package com.smartgrid.smartgrid.composite;

import java.math.BigDecimal;

/** Datos comunes de un nodo de la red. */
public abstract class ZonaRedNodo implements ComponenteRed {

    private final Long id;
    private final String nombre;
    private final String tipo;
    private final BigDecimal capacidadKw;
    /** Consumo de los medidores conectados directamente a este nodo. */
    protected final BigDecimal consumoDirecto;

    protected ZonaRedNodo(
            Long id,
            String nombre,
            String tipo,
            BigDecimal capacidadKw,
            BigDecimal consumoDirecto) {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.capacidadKw = capacidadKw;
        this.consumoDirecto = consumoDirecto;
    }

    @Override
    public Long getId() { return id; }

    @Override
    public String getNombre() { return nombre; }

    @Override
    public String getTipo() { return tipo; }

    @Override
    public BigDecimal getCapacidadKw() { return capacidadKw; }
}