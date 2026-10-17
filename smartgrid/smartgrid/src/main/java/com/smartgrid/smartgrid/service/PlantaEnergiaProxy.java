
package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class PlantaEnergiaProxy {

    private final PlantaEnergiaService plantaEnergiaService;
    private final AutorizacionService autorizacionService;

    public PlantaEnergiaProxy(
            PlantaEnergiaService plantaEnergiaService,
            AutorizacionService autorizacionService) {

        this.plantaEnergiaService = plantaEnergiaService;
        this.autorizacionService = autorizacionService;
    }

    public PlantaEnergia construirPlanta(
            Usuario usuario,
            String nombre,
            String fuente,
            Double capacidad,
            Boolean bateria,
            String ubicacion) {

        // El Proxy controla el acceso
        autorizacionService.validarRol(usuario, Rol.ADMIN);

        // Si está autorizado, delega al servicio real
        return plantaEnergiaService.construirPlanta(
                nombre,
                fuente,
                capacidad,
                bateria,
                ubicacion
        );
    }
}
