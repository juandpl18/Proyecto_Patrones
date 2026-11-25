package com.smartgrid.smartgrid.dto;

public record CrearClienteRequest(
        String documento,
        String nombre,
        String tipo,
        String direccion) {
}