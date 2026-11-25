package com.smartgrid.smartgrid.balanceo;

import com.smartgrid.smartgrid.chain.alivio.AlivioContexto;
import com.smartgrid.smartgrid.chain.alivio.CadenaAlivio;
import com.smartgrid.smartgrid.strategy.balanceo.BalanceoStrategy;
import com.smartgrid.smartgrid.strategy.balanceo.BalanceoStrategyRegistry;
import com.smartgrid.smartgrid.strategy.balanceo.PlanBalanceo;
import com.smartgrid.smartgrid.strategy.balanceo.ZonaBalance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class BalanceoPlanner {

    private final BalanceoStrategyRegistry estrategias;
    private final CadenaAlivio cadena;

    public BalanceoPlanner(
            BalanceoStrategyRegistry estrategias,
            CadenaAlivio cadena) {

        this.estrategias = estrategias;
        this.cadena = cadena;
    }

    public PlanCompleto planificar(
            AnalisisBalance analisis, String nombreEstrategia) {

        BalanceoStrategy estrategia = estrategias.para(nombreEstrategia);

        List<ZonaBalance> entrada = analisis.zonas().stream()
                .map(z -> new ZonaBalance(
                        z.zona().getId(),
                        z.zona().getNombre(),
                        z.balanceKw()))
                .toList();

        // 1. Strategy: reparte el superávit entre las zonas con déficit
        PlanBalanceo plan = estrategia.planificar(entrada);

        // 2. Chain: resuelve el déficit que las transferencias no cubrieron
        Map<Long, ZonaAnalizada> porId = analisis.porId();
        List<DeslastrePlan> deslastres = new ArrayList<>();
        List<String> observaciones = new ArrayList<>();

        for (Map.Entry<Long, Double> deficit
                : plan.deficitResidual().entrySet()) {

            ZonaAnalizada zona = porId.get(deficit.getKey());

            AlivioContexto contexto = cadena.evaluar(
                    zona.zona().getId(),
                    zona.zona().getNombre(),
                    deficit.getValue(),
                    zona.demandaEfectivaKw(),
                    zona.plantasRecuperables());

            if (contexto.getDeslastreKw() > Potencia.TOLERANCIA_KW) {
                deslastres.add(new DeslastrePlan(
                        zona.zona().getId(),
                        Potencia.redondear(contexto.getDeslastreKw())));
            }
            observaciones.addAll(contexto.getObservaciones());
        }

        return new PlanCompleto(
                estrategia.nombre(),
                plan.transferencias(),
                deslastres,
                observaciones);
    }
}