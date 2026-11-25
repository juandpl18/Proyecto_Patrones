package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String username,
        String rol,
        boolean activo) {

    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getUsername(),
                u.getRol().name(),
                u.isActivo());
    }
}