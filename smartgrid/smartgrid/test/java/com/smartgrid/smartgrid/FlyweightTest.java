
package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FlyweightTest {

    @Test
    void plantasSolaresCompartenLaMismaFuente() {
        PlantaEnergia planta1 = new PlantaEnergia.PlantaEnergiaBuilder(
                "PlantaSolar", "SOLAR"
        ).build();

        PlantaEnergia planta2 = new PlantaEnergia.PlantaEnergiaBuilder(
                "PlantaSolar2", "SOLAR"
        ).build();

        assertTrue(planta1.comparteFuenteCon(planta2));
    }
}
