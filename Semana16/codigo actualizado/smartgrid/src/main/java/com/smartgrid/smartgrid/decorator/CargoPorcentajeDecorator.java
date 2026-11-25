package com.smartgrid.smartgrid.decorator;

import com.smartgrid.smartgrid.facturacion.CalculoFactura;
import com.smartgrid.smartgrid.facturacion.LineaFactura;
import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Porcentaje del valor de la energía (negativo = descuento o subsidio). */
public class CargoPorcentajeDecorator extends CargoDecorator {

    private final String nombre;
    private final BigDecimal porcentaje;

    public CargoPorcentajeDecorator(
            CalculoFactura envuelto, String nombre, BigDecimal porcentaje) {

        super(envuelto);
        this.nombre = nombre;
        this.porcentaje = porcentaje;
    }

    @Override
    protected LineaFactura nuevaLinea(ResultadoCalculo base) {
        BigDecimal valor = base.energia()
                .multiply(porcentaje)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return new LineaFactura(
                nombre + " (" + porcentaje.stripTrailingZeros().toPlainString()
                        + "%)",
                valor);
    }
}