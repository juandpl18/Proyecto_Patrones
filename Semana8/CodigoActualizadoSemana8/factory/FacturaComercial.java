
package com.smartgrid.smartgrid.factory;

import com.smartgrid.smartgrid.dto.FacturaResultado;

public class FacturaComercial implements Factura {

    private static final double TARIFA = 700;

    @Override
    public FacturaResultado calcular(double consumo) {

        double total = consumo * TARIFA;

        return new FacturaResultado(
                "COMERCIAL",
                consumo,
                TARIFA,
                total
        );
    }
}