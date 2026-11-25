package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.chain.PlantaEnergiaValidacionChain;
import com.smartgrid.smartgrid.command.CrearPlantaCommand;
import com.smartgrid.smartgrid.command.PlantaEnergiaInvoker;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class PlantaEnergiaProxy {

    private final PlantaEnergiaService plantaEnergiaService;
    private final AutorizacionService autorizacionService;
    private final PlantaEnergiaValidacionChain validacionChain;
    private final PlantaEnergiaInvoker invoker;
    private final PlantaGeneracionService plantaGeneracionService;

    public PlantaEnergiaProxy(
            PlantaEnergiaService plantaEnergiaService,
            AutorizacionService autorizacionService,
            PlantaEnergiaValidacionChain validacionChain,
            PlantaEnergiaInvoker invoker,
            PlantaGeneracionService plantaGeneracionService) {

        this.plantaEnergiaService = plantaEnergiaService;
        this.autorizacionService = autorizacionService;
        this.validacionChain = validacionChain;
        this.invoker = invoker;
        this.plantaGeneracionService = plantaGeneracionService;
    }

    public PlantaGeneracion construirPlanta(
            Usuario usuario,
            String nombre,
            String fuente,
            Double capacidad,
            Boolean bateria,
            String ubicacion) {

        // 1. Proxy: verifica los permisos del usuario
        autorizacionService.validarRol(usuario, Rol.ADMIN);

        // 2. Chain of Responsibility: valida los datos
        validacionChain.validar(nombre, fuente, capacidad);

        // 3. Command: encapsula la operación de creación
        CrearPlantaCommand comando = new CrearPlantaCommand(
                plantaEnergiaService,
                nombre,
                fuente,
                capacidad,
                bateria,
                ubicacion
        );

        // 4. Invoker: ejecuta el comando (Builder + Flyweight)
        PlantaEnergia planta = invoker.ejecutar(comando);

        // 5. Persistencia: guarda la planta construida
        return plantaGeneracionService.registrar(planta, usuario.getUsername());
    }
}