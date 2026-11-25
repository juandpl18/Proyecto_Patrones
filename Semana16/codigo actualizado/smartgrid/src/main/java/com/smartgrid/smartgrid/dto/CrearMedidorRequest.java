package com.smartgrid.smartgrid.dto;

public record CrearMedidorRequest(
        String codigo,
        Long clienteId,
        Long zonaId) {
}