package com.smartgrid.smartgrid.dto;

import com.smartgrid.smartgrid.model.Rol;

public class LoginResponse {

    private final String mensaje;
    private final String usuario;
    private final Rol rol;
    private final String token;
    private final long expiraEnSegundos;

    public LoginResponse(
            String mensaje,
            String usuario,
            Rol rol,
            String token,
            long expiraEnSegundos) {

        this.mensaje = mensaje;
        this.usuario = usuario;
        this.rol = rol;
        this.token = token;
        this.expiraEnSegundos = expiraEnSegundos;
    }

    public String getMensaje() { return mensaje; }
    public String getUsuario() { return usuario; }
    public Rol getRol() { return rol; }
    public String getToken() { return token; }
    public long getExpiraEnSegundos() { return expiraEnSegundos; }
}