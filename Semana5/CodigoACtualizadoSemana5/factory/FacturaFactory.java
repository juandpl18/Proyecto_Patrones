package com.smartgrid.smartgrid.factory;

public abstract class FacturaFactory {

    // ===== FACTORY METHOD =====
    public abstract Factura crearFactura();

    public abstract String getTipo();

    public String generar(double consumo) {

        Factura factura = crearFactura();

        return factura.generarFactura(consumo);
    }
}