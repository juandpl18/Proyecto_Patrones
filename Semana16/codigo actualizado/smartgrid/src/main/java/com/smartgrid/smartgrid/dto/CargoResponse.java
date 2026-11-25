package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.CargoAdicional;

import java.math.BigDecimal;

public record CargoResponse(
        Long id,
        String codigo,
        String nombre,
        String tipoCliente,
        String tipoCalculo,
        BigDecimal valor,
        boolean activo) {

    public static CargoResponse desde(CargoAdicional c) {
        return new CargoResponse(
                c.getId(),
                c.getCodigo(),
                c.getNombre(),
                c.getTipoCliente() != null ? c.getTipoCliente().name() : null,
                c.getTipoCalculo().name(),
                c.getValor(),
                c.isActivo());
    }
}