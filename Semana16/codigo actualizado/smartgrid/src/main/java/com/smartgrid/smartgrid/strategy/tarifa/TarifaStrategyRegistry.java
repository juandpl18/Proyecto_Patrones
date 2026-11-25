package com.smartgrid.smartgrid.strategy.tarifa;

import com.smartgrid.smartgrid.model.ModalidadTarifa;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class TarifaStrategyRegistry {

    private final Map<ModalidadTarifa, TarifaStrategy> estrategias =
            new EnumMap<>(ModalidadTarifa.class);

    public TarifaStrategyRegistry(List<TarifaStrategy> disponibles) {
        disponibles.forEach(e -> estrategias.put(e.modalidad(), e));
    }

    public TarifaStrategy para(ModalidadTarifa modalidad) {
        TarifaStrategy estrategia = estrategias.get(modalidad);
        if (estrategia == null) {
            throw new IllegalStateException(
                    "No hay estrategia de cálculo para " + modalidad);
        }
        return estrategia;
    }
}