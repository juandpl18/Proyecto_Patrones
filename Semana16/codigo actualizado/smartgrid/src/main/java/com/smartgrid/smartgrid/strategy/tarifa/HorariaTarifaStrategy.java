package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.LecturaHora;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;

/** Cada lectura se cobra al precio de la franja horaria en que se midió. */
@Component
public class HorariaTarifaStrategy implements TarifaStrategy {

    private final ZoneId zona;

    public HorariaTarifaStrategy(
            @Value("${smartgrid.facturacion.zona-horaria:America/Bogota}")
            String zona) {

        this.zona = ZoneId.of(zona);
    }

    @Override
    public ModalidadTarifa modalidad() {
        return ModalidadTarifa.HORARIA;
    }

    @Override
    public BigDecimal calcularEnergia(
            TarifaConfig tarifa, DatosFacturacion datos) {

        if (datos.lecturas().isEmpty()) {
            throw new IllegalArgumentException(
                    "La tarifa horaria requiere lecturas del período "
                            + "(no aplica a la facturación estimada)");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (LecturaHora lectura : datos.lecturas()) {
            int hora = lectura.fechaHora().atZone(zona).getHour();

            TarifaConfig.Franja franja = tarifa.franjas().stream()
                    .filter(f -> f.cubre(hora))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "La tarifa no tiene franja para la hora " + hora));

            total = total.add(lectura.kwh().multiply(franja.precioKwh()));
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}