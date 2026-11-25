package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Cliente;

public record ClienteResponse(
        Long id,
        String documento,
        String nombre,
        String tipo,
        String direccion,
        boolean activo) {

    public static ClienteResponse desde(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getDocumento(),
                c.getNombre(),
                c.getTipo().name(),
                c.getDireccion(),
                c.isActivo());
    }
}