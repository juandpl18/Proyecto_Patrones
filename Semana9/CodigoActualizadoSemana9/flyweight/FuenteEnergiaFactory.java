
package com.smartgrid.smartgrid.flyweight;

import java.util.HashMap;
import java.util.Map;

public class FuenteEnergiaFactory {

    private final Map<String, FuenteEnergiaFlyweight> fuentes =
            new HashMap<>();

    public FuenteEnergiaFlyweight obtenerFuente(String tipo) {
        String clave = tipo.trim().toUpperCase();

        return fuentes.computeIfAbsent(clave, this::crearFuente);
    }

    private FuenteEnergiaFlyweight crearFuente(String tipo) {
        switch (tipo) {
            case "SOLAR":
                return new FuenteEnergiaFlyweight(
                        "SOLAR",
                        "Generación eléctrica mediante paneles fotovoltaicos",
                        "Utiliza la radiación solar"
                );

            case "EOLICA":
                return new FuenteEnergiaFlyweight(
                        "EOLICA",
                        "Generación eléctrica mediante aerogeneradores",
                        "Utiliza la energía del viento"
                );

            case "HIDRAULICA":
                return new FuenteEnergiaFlyweight(
                        "HIDRAULICA",
                        "Generación eléctrica mediante el movimiento del agua",
                        "Utiliza la energía hidráulica"
                );

            default:
                throw new IllegalArgumentException(
                        "Tipo de fuente energética no válido: " + tipo
                );
        }
    }

    public int cantidadFuentesCompartidas() {
        return fuentes.size();
    }
}
