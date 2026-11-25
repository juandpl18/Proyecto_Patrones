package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Medidor;

public record MedidorResponse(
        Long id,
        String codigo,
        Long clienteId,
        String clienteNombre,
        Long zonaId,
        String zonaNombre,
        boolean activo) {

    public static MedidorResponse desde(Medidor m) {
        return new MedidorResponse(
                m.getId(),
                m.getCodigo(),
                m.getCliente().getId(),
                m.getCliente().getNombre(),
                m.getZona().getId(),
                m.getZona().getNombre(),
                m.isActivo());
    }
}