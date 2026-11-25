package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.FacturaDetalle;
import com.smartgrid.smartgrid.model.FacturaEmitida;
import com.smartgrid.smartgrid.template.FacturaCalculada;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record FacturaResponse(
        Long id,
        Long clienteId,
        String clienteNombre,
        Long tarifaId,
        String tarifaNombre,
        String modalidad,
        String modo,
        LocalDate periodoInicio,
        LocalDate periodoFin,
        BigDecimal consumoKwh,
        BigDecimal precioPromedioKwh,
        BigDecimal energia,
        BigDecimal total,
        String estado,
        LocalDate fechaVencimiento,
        Instant emitidaEn,
        Instant pagadaEn,
        Instant anuladaEn,
        String motivoAnulacion,
        List<LineaResponse> lineas) {

    public record LineaResponse(String concepto, BigDecimal valor) {
    }

    /** Desde el cálculo del ciclo de facturación (simulado o recién emitido). */
    public static FacturaResponse desdeCalculo(FacturaCalculada c) {
        return new FacturaResponse(
                c.facturaId(),
                c.cliente().getId(),
                c.cliente().getNombre(),
                c.tarifa().getId(),
                c.tarifa().getNombre(),
                c.tarifa().getModalidad().name(),
                c.modo().name(),
                c.desde(),
                c.hasta(),
                c.consumoKwh(),
                c.precioPromedioKwh(),
                c.resultado().energia(),
                c.resultado().total(),
                c.estado(),
                c.vencimiento(),
                c.emitidaEn(),
                null,
                null,
                null,
                c.resultado().lineas().stream()
                        .map(l -> new LineaResponse(l.concepto(), l.valor()))
                        .toList());
    }

    /** Desde una factura guardada (con o sin sus líneas). */
    public static FacturaResponse desdeEntidad(
            FacturaEmitida f, List<FacturaDetalle> detalle) {

        return new FacturaResponse(
                f.getId(),
                f.getCliente().getId(),
                f.getCliente().getNombre(),
                f.getTarifa().getId(),
                f.getTarifa().getNombre(),
                f.getTarifa().getModalidad().name(),
                f.getModo().name(),
                f.getPeriodoInicio(),
                f.getPeriodoFin(),
                f.getConsumoKwh(),
                f.getPrecioKwh(),
                f.getEnergia(),
                f.getTotal(),
                f.getEstado().name(),
                f.getFechaVencimiento(),
                f.getEmitidaEn(),
                f.getPagadaEn(),
                f.getAnuladaEn(),
                f.getMotivoAnulacion(),
                detalle.stream()
                        .map(d -> new LineaResponse(d.getConcepto(), d.getValor()))
                        .toList());
    }
}