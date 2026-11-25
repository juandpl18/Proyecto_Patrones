package com.smartgrid.smartgrid.template;

import com.smartgrid.smartgrid.model.ModoFacturacion;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlantillaFacturacionRegistry {

    private final Map<ModoFacturacion, PlantillaFacturacion> plantillas =
            new EnumMap<>(ModoFacturacion.class);

    public PlantillaFacturacionRegistry(List<PlantillaFacturacion> disponibles) {
        disponibles.forEach(p -> plantillas.put(p.modo(), p));
    }

    /** Sin modo = LECTURAS. */
    public PlantillaFacturacion para(String modo) {
        String clave = modo == null || modo.isBlank()
                ? ModoFacturacion.LECTURAS.name()
                : modo.trim().toUpperCase();

        try {
            return plantillas.get(ModoFacturacion.valueOf(clave));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Modo inválido. Opciones: LECTURAS o ESTIMADA");
        }
    }
}