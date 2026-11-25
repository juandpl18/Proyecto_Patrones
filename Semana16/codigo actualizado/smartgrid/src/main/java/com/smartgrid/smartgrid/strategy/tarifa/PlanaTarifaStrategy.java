package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PlanaTarifaStrategy implements TarifaStrategy {

    @Override
    public ModalidadTarifa modalidad() {
        return ModalidadTarifa.PLANA;
    }

    @Override
    public BigDecimal calcularEnergia(
            TarifaConfig tarifa, DatosFacturacion datos) {

        return datos.consumoKwh()
                .multiply(tarifa.precioKwh())
                .setScale(2, RoundingMode.HALF_UP);
    }
}