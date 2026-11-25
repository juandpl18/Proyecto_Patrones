package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.model.ModalidadTarifa;

import java.math.BigDecimal;

/** Algoritmo que calcula el valor de la energía según la modalidad. */
public interface TarifaStrategy {

    ModalidadTarifa modalidad();

    BigDecimal calcularEnergia(TarifaConfig tarifa, DatosFacturacion datos);
}