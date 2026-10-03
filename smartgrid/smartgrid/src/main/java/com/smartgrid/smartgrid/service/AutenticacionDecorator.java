package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.model.Usuario;

public abstract class AutenticacionDecorator implements Autenticacion {

    protected final Autenticacion autenticacion;

    protected AutenticacionDecorator(Autenticacion autenticacion) {
        this.autenticacion = autenticacion;
    }

    @Override
    public abstract Usuario autenticar(String username, String password);
}