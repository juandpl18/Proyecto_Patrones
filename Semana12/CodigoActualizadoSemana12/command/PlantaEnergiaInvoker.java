package com.smartgrid.smartgrid.command;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import org.springframework.stereotype.Component;

@Component
public class PlantaEnergiaInvoker {

    public PlantaEnergia ejecutar(Comando comando) {

        return comando.ejecutar();

    }
}