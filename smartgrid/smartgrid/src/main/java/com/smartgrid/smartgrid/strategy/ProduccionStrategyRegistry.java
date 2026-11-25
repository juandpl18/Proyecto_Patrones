package com.smartgrid.smartgrid.strategy;

import com.smartgrid.smartgrid.model.TipoFuente;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Elige la estrategia según la fuente de la planta. */
@Component
public class ProduccionStrategyRegistry {

    private final Map<TipoFuente, ProduccionStrategy> estrategias =
            new EnumMap<>(TipoFuente.class);

    public ProduccionStrategyRegistry(List<ProduccionStrategy> disponibles) {
        disponibles.forEach(e -> estrategias.put(e.tipo(), e));
    }

    public ProduccionStrategy para(TipoFuente tipo) {
        ProduccionStrategy estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalStateException(
                    "No hay estrategia de producción para " + tipo);
        }
        return estrategia;
    }
}