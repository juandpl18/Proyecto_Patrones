package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.model.Usuario;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AutenticacionService {

    private final Autenticacion autenticacion;

    public AutenticacionService(
            @Qualifier("auditoriaLoginDecorator")
            Autenticacion autenticacion) {

        this.autenticacion = autenticacion;
    }

    public Usuario autenticar(String username, String password) {
        return autenticacion.autenticar(username, password);
    }
}