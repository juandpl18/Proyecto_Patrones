package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.model.Usuario;

public interface Autenticacion {

    Usuario autenticar(String username, String password);
}