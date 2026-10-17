
package com.smartgrid.smartgrid.factory;

import com.smartgrid.smartgrid.dto.FacturaResultado;

public class FacturaResidencial implements Factura {

    private static final double TARIFA = 500;

    @Override
    public FacturaResultado calcular(double consumo) {

        double total = consumo * TARIFA;

        return new FacturaResultado(
                "RESIDENCIAL",
                consumo,
                TARIFA,
                total
        );
    }
}