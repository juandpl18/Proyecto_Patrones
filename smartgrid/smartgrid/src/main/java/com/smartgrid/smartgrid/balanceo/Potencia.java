package com.smartgrid.smartgrid.balanceo;

import com.smartgrid.smartgrid.model.EstadoBalance;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Constantes y conversiones comunes del cálculo de potencia (kW). */
public final class Potencia {

    /** Diferencias menores a esto se consideran equilibrio. */
    public static final double TOLERANCIA_KW = 0.001;

    private Potencia() {
    }

    public static double redondear(double valor) {
        return Math.round(valor * 1000.0) / 1000.0;
    }

    public static BigDecimal kw(double valor) {
        return BigDecimal.valueOf(valor).setScale(3, RoundingMode.HALF_UP);
    }

    public static EstadoBalance estadoDe(double balanceKw) {
        if (balanceKw > TOLERANCIA_KW) {
            return EstadoBalance.SUPERAVIT;
        }
        if (balanceKw < -TOLERANCIA_KW) {
            return EstadoBalance.DEFICIT;
        }
        return EstadoBalance.EQUILIBRADO;
    }
}