package com.smartgrid.smartgrid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record LoteResponse(
        LocalDate desde,
        LocalDate hasta,
        String modo,
        int clientesProcesados,
        int facturasEmitidas,
        int clientesOmitidos,
        BigDecimal totalFacturado,
        List<Emitida> emitidas,
        List<Omitida> omitidas) {

    public record Emitida(
            Long clienteId,
            String clienteNombre,
            Long facturaId,
            BigDecimal total) {
    }

    public record Omitida(
            Long clienteId,
            String clienteNombre,
            String motivo) {
    }
}