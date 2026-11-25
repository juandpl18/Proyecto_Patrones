package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Cada kWh se cobra al precio del tramo en el que cae. */
@Component
public class EscalonadaTarifaStrategy implements TarifaStrategy {

    @Override
    public ModalidadTarifa modalidad() {
        return ModalidadTarifa.ESCALONADA;
    }

    @Override
    public BigDecimal calcularEnergia(
            TarifaConfig tarifa, DatosFacturacion datos) {

        BigDecimal consumo = datos.consumoKwh();
        BigDecimal total = BigDecimal.ZERO;

        for (TarifaConfig.Tramo tramo : tarifa.tramos()) {
            BigDecimal tope = tramo.hastaKwh() == null
                    ? consumo : consumo.min(tramo.hastaKwh());
            BigDecimal kwhEnTramo = tope.subtract(tramo.desdeKwh());

            if (kwhEnTramo.signum() > 0) {
                total = total.add(kwhEnTramo.multiply(tramo.precioKwh()));
            }
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}