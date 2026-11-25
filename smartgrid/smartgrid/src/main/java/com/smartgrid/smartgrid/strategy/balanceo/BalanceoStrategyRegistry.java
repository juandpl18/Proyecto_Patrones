package com.smartgrid.smartgrid.strategy.balanceo;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Elige la estrategia por nombre. Una estrategia nueva no obliga a modificar esta clase. */
@Component
public class BalanceoStrategyRegistry {

    private final Map<String, BalanceoStrategy> estrategias =
            new LinkedHashMap<>();

    public BalanceoStrategyRegistry(List<BalanceoStrategy> disponibles) {
        disponibles.forEach(e -> estrategias.put(e.nombre(), e));
    }

    public BalanceoStrategy para(String nombre) {
        String clave = nombre == null ? "" : nombre.trim().toUpperCase();
        BalanceoStrategy estrategia = estrategias.get(clave);

        if (estrategia == null) {
            throw new IllegalArgumentException(
                    "Estrategia inválida. Opciones: " + estrategias.keySet());
        }
        return estrategia;
    }

    public Collection<BalanceoStrategy> todas() {
        return estrategias.values();
    }
}