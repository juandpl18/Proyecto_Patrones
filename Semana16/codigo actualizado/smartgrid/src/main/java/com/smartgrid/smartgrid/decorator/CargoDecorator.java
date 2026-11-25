package com.smartgrid.smartgrid.decorator;

import com.smartgrid.smartgrid.facturacion.CalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.LineaFactura;
import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;

import java.util.ArrayList;
import java.util.List;

/** Envuelve un cálculo y le agrega una línea, sin modificar lo anterior. */
public abstract class CargoDecorator implements CalculoFactura {

    private final CalculoFactura envuelto;

    protected CargoDecorator(CalculoFactura envuelto) {
        this.envuelto = envuelto;
    }

    @Override
    public final ResultadoCalculo calcular(DatosFacturacion datos) {

        ResultadoCalculo base = envuelto.calcular(datos);

        List<LineaFactura> lineas = new ArrayList<>(base.lineas());
        lineas.add(nuevaLinea(base));

        return new ResultadoCalculo(base.energia(), lineas);
    }

    protected abstract LineaFactura nuevaLinea(ResultadoCalculo base);
}