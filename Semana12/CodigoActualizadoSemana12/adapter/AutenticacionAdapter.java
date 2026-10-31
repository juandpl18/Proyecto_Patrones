package com.smartgrid.smartgrid.adapter;

import com.smartgrid.smartgrid.model.Usuario;

public interface AutenticacionAdapter {

    Usuario autenticar(String username, String password);
}