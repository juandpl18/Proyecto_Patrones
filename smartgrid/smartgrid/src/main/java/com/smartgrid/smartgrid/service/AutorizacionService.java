
package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AutorizacionService {

    public boolean tieneRol(Usuario usuario, Rol rolRequerido) {

        return usuario != null
                && usuario.isActivo()
                && usuario.getRol() == rolRequerido;
    }

    public void validarRol(Usuario usuario, Rol rolRequerido) {

        if (!tieneRol(usuario, rolRequerido)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no tiene permisos para realizar esta operación"
            );
        }
    }
}
