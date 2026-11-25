package com.smartgrid.smartgrid.decorator;

import com.smartgrid.smartgrid.facturacion.CalculoFactura;
import com.smartgrid.smartgrid.facturacion.LineaFactura;
import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Valor fijo en pesos por factura. */
public class CargoFijoDecorator extends CargoDecorator {

    private final String nombre;
    private final BigDecimal valor;

    public CargoFijoDecorator(
            CalculoFactura envuelto, String nombre, BigDecimal valor) {

        super(envuelto);
        this.nombre = nombre;
        this.valor = valor;
    }

    @Override
    protected LineaFactura nuevaLinea(ResultadoCalculo base) {
        return new LineaFactura(
                nombre, valor.setScale(2, RoundingMode.HALF_UP));
    }
}