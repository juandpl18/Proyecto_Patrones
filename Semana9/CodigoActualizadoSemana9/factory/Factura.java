
package com.smartgrid.smartgrid.factory;

import com.smartgrid.smartgrid.dto.FacturaResultado;

public interface Factura {

    FacturaResultado calcular(double consumo);

    default String generarFactura(double consumo) {

        FacturaResultado resultado = calcular(consumo);

        return "FACTURA " + resultado.getTipoFactura() + "\n"
                + "Consumo: " + resultado.getConsumo() + " kWh\n"
                + "Tarifa: $" + resultado.getTarifa() + " por kWh\n"
                + "Total: $" + resultado.getTotal();
    }
}