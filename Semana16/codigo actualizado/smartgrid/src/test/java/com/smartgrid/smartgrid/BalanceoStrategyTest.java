package com.smartgrid.smartgrid;

import com.smartgrid.smartgrid.strategy.balanceo.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalanceoStrategyTest {

    private final ProporcionalBalanceoStrategy proporcional =
            new ProporcionalBalanceoStrategy();
    private final MayorDeficitBalanceoStrategy mayorDeficit =
            new MayorDeficitBalanceoStrategy();

    private double kw(PlanBalanceo plan, Long origen, Long destino) {
        return plan.transferencias().stream()
                .filter(t -> t.origenId().equals(origen)
                        && t.destinoId().equals(destino))
                .mapToDouble(TransferenciaPlan::kw)
                .sum();
    }

    @Test
    void proporcionalCubreTodoElDeficitSiHaySuficienteSuperavit() {
        PlanBalanceo plan = proporcional.planificar(List.of(
                new ZonaBalance(1L, "A", 100),
                new ZonaBalance(2L, "B", -60),
                new ZonaBalance(3L, "C", -40)));

        assertEquals(60.0, kw(plan, 1L, 2L), 0.001);
        assertEquals(40.0, kw(plan, 1L, 3L), 0.001);
        assertTrue(plan.deficitResidual().isEmpty());
    }

    @Test
    void proporcionalRepartePorIgualElDeficitCuandoNoAlcanza() {
        PlanBalanceo plan = proporcional.planificar(List.of(
                new ZonaBalance(1L, "A", 50),
                new ZonaBalance(2L, "B", -60),
                new ZonaBalance(3L, "C", -40)));

        assertEquals(30.0, kw(plan, 1L, 2L), 0.001);
        assertEquals(20.0, kw(plan, 1L, 3L), 0.001);
        assertEquals(30.0, plan.deficitResidual().get(2L), 0.001);
        assertEquals(20.0, plan.deficitResidual().get(3L), 0.001);
    }

    @Test
    void sinSuperavitTodoElDeficitQuedaPendiente() {
        PlanBalanceo plan = proporcional.planificar(List.of(
                new ZonaBalance(2L, "B", -60),
                new ZonaBalance(3L, "C", -40)));

        assertTrue(plan.transferencias().isEmpty());
        assertEquals(60.0, plan.deficitResidual().get(2L), 0.001);
        assertEquals(40.0, plan.deficitResidual().get(3L), 0.001);
    }

    @Test
    void sinDeficitNoHayTransferencias() {
        PlanBalanceo plan = proporcional.planificar(List.of(
                new ZonaBalance(1L, "A", 100),
                new ZonaBalance(2L, "B", 20)));

        assertTrue(plan.transferencias().isEmpty());
        assertTrue(plan.deficitResidual().isEmpty());
    }

    @Test
    void mayorDeficitAtiendeAlMasAfectadoYAgotaElMayorSuperavit() {
        PlanBalanceo plan = mayorDeficit.planificar(List.of(
                new ZonaBalance(1L, "A", 80),
                new ZonaBalance(2L, "B", 20),
                new ZonaBalance(3L, "C", -60),
                new ZonaBalance(4L, "D", -30)));

        assertEquals(60.0, kw(plan, 1L, 3L), 0.001);
        assertEquals(20.0, kw(plan, 1L, 4L), 0.001);
        assertEquals(10.0, kw(plan, 2L, 4L), 0.001);
        assertTrue(plan.deficitResidual().isEmpty());
    }

    @Test
    void mayorDeficitInformaLoQueNoPudoCubrir() {
        PlanBalanceo plan = mayorDeficit.planificar(List.of(
                new ZonaBalance(1L, "A", 40),
                new ZonaBalance(3L, "C", -60)));

        assertEquals(40.0, kw(plan, 1L, 3L), 0.001);
        assertEquals(20.0, plan.deficitResidual().get(3L), 0.001);
    }

    @Test
    void elRegistroRechazaUnaEstrategiaInexistente() {
        BalanceoStrategyRegistry registro = new BalanceoStrategyRegistry(
                List.of(proporcional, mayorDeficit));

        assertSame(proporcional, registro.para("proporcional"));
        assertThrows(IllegalArgumentException.class,
                () -> registro.para("INEXISTENTE"));
    }
}