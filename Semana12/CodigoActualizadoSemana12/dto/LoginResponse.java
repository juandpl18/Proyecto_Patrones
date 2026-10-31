package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Rol;

public class LoginResponse {

    private String mensaje;
    private String usuario;
    private Rol rol;

    public LoginResponse(String mensaje, String usuario, Rol rol) {
        this.mensaje = mensaje;
        this.usuario = usuario;
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getUsuario() {
        return usuario;
    }

    public Rol getRol() {
        return rol;
    }
}
