package com.smartgrid.smartgrid.facturacion;

import com.smartgrid.smartgrid.decorator.CargoFijoDecorator;
import com.smartgrid.smartgrid.decorator.CargoPorcentajeDecorator;
import com.smartgrid.smartgrid.model.CargoAdicional;
import com.smartgrid.smartgrid.model.TipoCalculoCargo;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.repository.CargoAdicionalRepository;
import com.smartgrid.smartgrid.strategy.tarifa.TarifaConfig;
import com.smartgrid.smartgrid.strategy.tarifa.TarifaStrategyRegistry;
import org.springframework.stereotype.Component;

@Component
public class ConstructorCalculoFactura {

    private final TarifaStrategyRegistry estrategias;
    private final CargoAdicionalRepository cargoRepository;

    public ConstructorCalculoFactura(
            TarifaStrategyRegistry estrategias,
            CargoAdicionalRepository cargoRepository) {

        this.estrategias = estrategias;
        this.cargoRepository = cargoRepository;
    }

    /** Strategy para la energía + un Decorator por cada cargo aplicable. */
    public CalculoFactura construir(TarifaConfig tarifa, TipoCliente tipo) {

        CalculoFactura calculo = new EnergiaCalculo(
                estrategias.para(tarifa.modalidad()), tarifa);

        for (CargoAdicional cargo : cargoRepository.findByActivoTrueOrderById()) {

            boolean aplica = cargo.getTipoCliente() == null
                    || cargo.getTipoCliente() == tipo;
            if (!aplica) {
                continue;
            }

            calculo = cargo.getTipoCalculo() == TipoCalculoCargo.PORCENTAJE
                    ? new CargoPorcentajeDecorator(
                    calculo, cargo.getNombre(), cargo.getValor())
                    : new CargoFijoDecorator(
                    calculo, cargo.getNombre(), cargo.getValor());
        }

        return calculo;
    }
}