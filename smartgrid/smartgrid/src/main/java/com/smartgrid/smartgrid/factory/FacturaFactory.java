
package com.smartgrid.smartgrid.factory;

import com.smartgrid.smartgrid.dto.FacturaResultado;

public abstract class FacturaFactory {

    public abstract Factura crearFactura();

    public abstract String getTipo();

    public FacturaResultado generarResultado(double consumo) {
        return crearFactura().calcular(consumo);
    }

    public String generar(double consumo) {
        return crearFactura().generarFactura(consumo);
    }
}