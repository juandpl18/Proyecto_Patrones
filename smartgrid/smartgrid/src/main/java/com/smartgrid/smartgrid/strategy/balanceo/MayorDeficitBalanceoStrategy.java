package com.smartgrid.smartgrid.strategy.balanceo;

import com.smartgrid.smartgrid.balanceo.Potencia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Atiende primero a la zona con mayor déficit, tomando energía de la zona
 * con mayor superávit hasta agotarla, y así sucesivamente.
 */
@Component
public class MayorDeficitBalanceoStrategy implements BalanceoStrategy {

    @Override
    public String nombre() {
        return "MAYOR_DEFICIT";
    }

    @Override
    public String descripcion() {
        return "Cubre primero la zona con mayor déficit, desde la de mayor superávit";
    }

    @Override
    public PlanBalanceo planificar(List<ZonaBalance> zonas) {

        List<ZonaBalance> excedentes = zonas.stream()
                .filter(z -> z.balanceKw() > Potencia.TOLERANCIA_KW)
                .sorted(Comparator.comparingDouble(ZonaBalance::balanceKw)
                        .reversed())
                .toList();

        List<ZonaBalance> deficits = zonas.stream()
                .filter(z -> z.balanceKw() < -Potencia.TOLERANCIA_KW)
                .sorted(Comparator.comparingDouble(ZonaBalance::balanceKw))
                .toList();

        Map<Long, Double> disponible = new LinkedHashMap<>();
        excedentes.forEach(e -> disponible.put(e.zonaId(), e.balanceKw()));

        List<TransferenciaPlan> transferencias = new ArrayList<>();
        Map<Long, Double> residual = new LinkedHashMap<>();

        for (ZonaBalance deficit : deficits) {
            double falta = -deficit.balanceKw();

            for (Map.Entry<Long, Double> origen : disponible.entrySet()) {
                if (falta <= Potencia.TOLERANCIA_KW) {
                    break;
                }
                double puede = origen.getValue();
                if (puede <= Potencia.TOLERANCIA_KW) {
                    continue;
                }

                double kw = Math.min(puede, falta);
                transferencias.add(new TransferenciaPlan(
                        origen.getKey(), deficit.zonaId(),
                        Potencia.redondear(kw)));
                origen.setValue(puede - kw);
                falta -= kw;
            }

            if (falta > Potencia.TOLERANCIA_KW) {
                residual.put(deficit.zonaId(), Potencia.redondear(falta));
            }
        }

        return new PlanBalanceo(transferencias, residual);
    }
}