package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.model.Usuario;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("auditoriaLoginDecorator")
public class AuditoriaLoginDecorator extends AutenticacionDecorator {

    public AuditoriaLoginDecorator(
            @Qualifier("autenticacionLocalAdapter")
            Autenticacion autenticacion) {

        super(autenticacion);
    }

    @Override
    public Usuario autenticar(String username, String password) {

        Usuario usuario = autenticacion.autenticar(username, password);

        if (usuario != null) {
            System.out.println(
                    "[AUDITORIA] Inicio de sesión exitoso: " + username
            );
        } else {
            System.out.println(
                    "[AUDITORIA] Intento de inicio de sesión fallido: " + username
            );
        }

        return usuario;
    }
}