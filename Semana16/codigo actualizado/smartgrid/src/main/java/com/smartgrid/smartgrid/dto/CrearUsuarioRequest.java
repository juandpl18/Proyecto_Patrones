package com.smartgrid.smartgrid.dto;

public record CrearUsuarioRequest(
        String nombre,
        String username,
        String password,
        String rol) {
}