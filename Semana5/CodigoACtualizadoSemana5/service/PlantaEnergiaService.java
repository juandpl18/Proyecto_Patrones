package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import org.springframework.stereotype.Service;

@Service
public class PlantaEnergiaService {

    public PlantaEnergia construirPlanta(String nombre, String fuente, Double capacidad, Boolean bateria, String ubicacion) {
    
        PlantaEnergia.PlantaEnergiaBuilder builder = new PlantaEnergia.PlantaEnergiaBuilder(nombre, fuente);

        // Configuración encadenada de atributos opcionales
        if (capacidad != null) builder.capacidadKw(capacidad);
        if (bateria != null) builder.tieneBateriaReserva(bateria);
        if (ubicacion != null) builder.ubicacion(ubicacion);

        return builder.build();
    }
}