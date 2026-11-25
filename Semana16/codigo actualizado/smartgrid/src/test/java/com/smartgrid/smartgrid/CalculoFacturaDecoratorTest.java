package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.decorator.CargoFijoDecorator;
import com.smartgrid.smartgrid.decorator.CargoPorcentajeDecorator;
import com.smartgrid.smartgrid.facturacion.CalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.EnergiaCalculo;
import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.strategy.tarifa.PlanaTarifaStrategy;
import com.smartgrid.smartgrid.strategy.tarifa.TarifaConfig;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculoFacturaDecoratorTest {

    private final DatosFacturacion datos = new DatosFacturacion(
            TipoCliente.RESIDENCIAL, new BigDecimal("100"), List.of());

    private CalculoFactura base() {
        TarifaConfig config = new TarifaConfig(ModalidadTarifa.PLANA, "Base",
                new BigDecimal("500"), List.of(), List.of());
        return new EnergiaCalculo(new PlanaTarifaStrategy(), config);
    }

    private void assertValor(String esperado, BigDecimal real) {
        assertEquals(0, new BigDecimal(esperado).compareTo(real),
                "esperado " + esperado + " pero fue " + real);
    }

    @Test
    void sinDecoradoresSoloSeCobraLaEnergia() {
        ResultadoCalculo r = base().calcular(datos);

        assertEquals(1, r.lineas().size());
        assertValor("50000", r.total());
    }

    @Test
    void cadaDecoradorAgregaSuLineaSobreElValorDeLaEnergia() {
        CalculoFactura calculo = new CargoFijoDecorator(
                new CargoPorcentajeDecorator(
                        new CargoPorcentajeDecorator(
                                base(), "Alumbrado", new BigDecimal("10")),
                        "Subsidio", new BigDecimal("-20")),
                "Cargo fijo", new BigDecimal("1500"));

        ResultadoCalculo r = calculo.calcular(datos);

        assertEquals(4, r.lineas().size());
        assertValor("50000", r.energia());
        assertValor("5000", r.lineas().get(1).valor());     // 10% de 50000
        assertValor("-10000", r.lineas().get(2).valor());   // -20% de 50000
        assertValor("1500", r.lineas().get(3).valor());
        assertValor("46500", r.total());
    }

    @Test
    void elConceptoMuestraElPorcentaje() {
        ResultadoCalculo r = new CargoPorcentajeDecorator(
                base(), "Alumbrado", new BigDecimal("5.0000")).calcular(datos);

        assertEquals("Alumbrado (5%)", r.lineas().get(1).concepto());
    }
}