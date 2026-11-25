package com.smartgrid.smartgrid.facturacion;

import com.smartgrid.smartgrid.strategy.tarifa.TarifaConfig;
import com.smartgrid.smartgrid.strategy.tarifa.TarifaStrategy;

import java.math.BigDecimal;
import java.util.List;

public class EnergiaCalculo implements CalculoFactura {

    private final TarifaStrategy estrategia;
    private final TarifaConfig tarifa;

    public EnergiaCalculo(TarifaStrategy estrategia, TarifaConfig tarifa) {
        this.estrategia = estrategia;
        this.tarifa = tarifa;
    }

    @Override
    public ResultadoCalculo calcular(DatosFacturacion datos) {
        BigDecimal energia = estrategia.calcularEnergia(tarifa, datos);

        return new ResultadoCalculo(energia, List.of(new LineaFactura(
                "Energía activa (" + tarifa.modalidad() + ")", energia)));
    }
}