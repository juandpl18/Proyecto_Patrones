package com.smartgrid.smartgrid.strategy.balanceo;

import com.smartgrid.smartgrid.balanceo.Potencia;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cada zona en déficit recibe el mismo porcentaje de cobertura, y cada
 * zona con superávit aporta en proporción a su excedente.
 */
@Component
public class ProporcionalBalanceoStrategy implements BalanceoStrategy {

    @Override
    public String nombre() {
        return "PROPORCIONAL";
    }

    @Override
    public String descripcion() {
        return "Reparte el superávit en proporción al déficit de cada zona";
    }

    @Override
    public PlanBalanceo planificar(List<ZonaBalance> zonas) {

        List<ZonaBalance> excedentes = zonas.stream()
                .filter(z -> z.balanceKw() > Potencia.TOLERANCIA_KW)
                .toList();
        List<ZonaBalance> deficits = zonas.stream()
                .filter(z -> z.balanceKw() < -Potencia.TOLERANCIA_KW)
                .toList();

        double totalExcedente = excedentes.stream()
                .mapToDouble(ZonaBalance::balanceKw).sum();
        double totalDeficit = deficits.stream()
                .mapToDouble(z -> -z.balanceKw()).sum();

        List<TransferenciaPlan> transferencias = new ArrayList<>();
        Map<Long, Double> residual = new LinkedHashMap<>();

        if (totalExcedente <= 0) {
            deficits.forEach(d ->
                    residual.put(d.zonaId(), Potencia.redondear(-d.balanceKw())));
            return new PlanBalanceo(transferencias, residual);
        }

        double cubierto = Math.min(totalExcedente, totalDeficit);

        for (ZonaBalance deficit : deficits) {
            double necesita = -deficit.balanceKw();
            double recibe = cubierto * necesita / totalDeficit;

            for (ZonaBalance excedente : excedentes) {
                double kw = Potencia.redondear(
                        recibe * excedente.balanceKw() / totalExcedente);
                if (kw > Potencia.TOLERANCIA_KW) {
                    transferencias.add(new TransferenciaPlan(
                            excedente.zonaId(), deficit.zonaId(), kw));
                }
            }

            double faltante = Potencia.redondear(necesita - recibe);
            if (faltante > Potencia.TOLERANCIA_KW) {
                residual.put(deficit.zonaId(), faltante);
            }
        }

        return new PlanBalanceo(transferencias, residual);
    }
}