package com.smartgrid.smartgrid.template;

import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.ModoFacturacion;
import com.smartgrid.smartgrid.model.Tarifa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;

/** Resultado del ciclo de facturación: simulado (sin id) o ya emitido. */
public record FacturaCalculada(
        Long facturaId,
        Cliente cliente,
        Tarifa tarifa,
        ModoFacturacion modo,
        LocalDate desde,
        LocalDate hasta,
        BigDecimal consumoKwh,
        ResultadoCalculo resultado,
        LocalDate vencimiento,
        String estado,
        Instant emitidaEn) {

    public BigDecimal precioPromedioKwh() {
        return consumoKwh.signum() > 0
                ? resultado.energia().divide(consumoKwh, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

    public FacturaCalculada emitida(Long id, Instant emitidaEn) {
        return new FacturaCalculada(id, cliente, tarifa, modo, desde, hasta,
                consumoKwh, resultado, vencimiento, "EMITIDA", emitidaEn);
    }
}