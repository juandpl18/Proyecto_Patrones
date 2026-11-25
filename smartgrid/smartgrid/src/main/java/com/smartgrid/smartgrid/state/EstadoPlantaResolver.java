package com.smartgrid.smartgrid.state;

import com.smartgrid.smartgrid.model.EstadoPlanta;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Entrega el objeto State que corresponde a cada valor del enum.
 * Spring inyecta todos los EstadoPlantaState: un estado nuevo
 * no obliga a modificar esta clase.
 */
@Component
public class EstadoPlantaResolver {

    private final Map<EstadoPlanta, EstadoPlantaState> estados =
            new EnumMap<>(EstadoPlanta.class);

    public EstadoPlantaResolver(List<EstadoPlantaState> disponibles) {
        disponibles.forEach(e -> estados.put(e.estado(), e));
    }

    public EstadoPlantaState de(EstadoPlanta estado) {
        EstadoPlantaState state = estados.get(estado);
        if (state == null) {
            throw new IllegalStateException(
                    "No hay comportamiento definido para el estado " + estado);
        }
        return state;
    }
}