package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.UsuarioRepository;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.service.AutorizacionService;
import com.smartgrid.smartgrid.service.PlantaEnergiaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/planta")
@CrossOrigin(origins = "*")
public class PlantaEnergiaController {

    private final PlantaEnergiaService plantaEnergiaService;
    private final UsuarioRepository usuarioRepository;
    private final AutorizacionService autorizacionService;

    public PlantaEnergiaController(
            PlantaEnergiaService plantaEnergiaService,
            UsuarioRepository usuarioRepository,
            AutorizacionService autorizacionService) {

        this.plantaEnergiaService = plantaEnergiaService;
        this.usuarioRepository = usuarioRepository;
        this.autorizacionService = autorizacionService;
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

        autorizacionService.validarRol(usuario, Rol.ADMIN);

        return plantaEnergiaService.construirPlanta(
                nombre,
                fuente,
                capacidad,
                bateria,
                ubicacion
        );
    }
}