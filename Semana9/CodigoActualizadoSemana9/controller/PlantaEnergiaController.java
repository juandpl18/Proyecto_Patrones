
package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.UsuarioRepository;
import com.smartgrid.smartgrid.service.PlantaEnergiaProxy;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planta")
@CrossOrigin(origins = "*")
public class PlantaEnergiaController {

    private final PlantaEnergiaProxy plantaEnergiaProxy;
    private final UsuarioRepository usuarioRepository;

    public PlantaEnergiaController(
            PlantaEnergiaProxy plantaEnergiaProxy,
            UsuarioRepository usuarioRepository) {

        this.plantaEnergiaProxy = plantaEnergiaProxy;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/crear")
    public PlantaEnergia crearPlanta(
            @RequestParam String username,
            @RequestParam String nombre,
            @RequestParam String fuente,
            @RequestParam(required = false) Double capacidad,
            @RequestParam(required = false) Boolean bateria,
            @RequestParam(required = false) String ubicacion) {

        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        return plantaEnergiaProxy.construirPlanta(
                usuario,
                nombre,
                fuente,
                capacidad,
                bateria,
                ubicacion
        );
    }
}
