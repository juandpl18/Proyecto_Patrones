package com.smartgrid.smartgrid.chain.alivio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Datos que viajan por la cadena para una zona con déficit pendiente. */
public class AlivioContexto {

    private final Long zonaId;
    private final String zonaNombre;
    private final double demandaKw;
    private final List<PlantaRecuperable> plantasRecuperables;
    private final List<String> observaciones = new ArrayList<>();

    private double deficitPendiente;
    private double deslastreKw;

    public AlivioContexto(
            Long zonaId,
            String zonaNombre,
            double deficitKw,
            double demandaKw,
            List<PlantaRecuperable> plantasRecuperables) {

        this.zonaId = zonaId;
        this.zonaNombre = zonaNombre;
        this.deficitPendiente = deficitKw;
        this.demandaKw = demandaKw;
        this.plantasRecuperables = plantasRecuperables;
    }

    public void aceptarDeficit(String motivo) {
        observaciones.add("Zona '" + zonaNombre + "': " + motivo);
        deficitPendiente = 0;
    }

    public void recomendar(String texto) {
        observaciones.add("Zona '" + zonaNombre + "': " + texto);
    }

    public void deslastrar(double kw) {
        deslastreKw += kw;
        deficitPendiente = Math.max(0, deficitPendiente - kw);
        observaciones.add(String.format(Locale.ROOT,
                "Zona '%s': deslastre de %.3f kW", zonaNombre, kw));
    }

    public Long getZonaId() { return zonaId; }

    public String getZonaNombre() { return zonaNombre; }

    public double getDemandaKw() { return demandaKw; }

    public double getDeficitPendiente() { return deficitPendiente; }

    public double getDeslastreKw() { return deslastreKw; }

    public List<PlantaRecuperable> getPlantasRecuperables() {
        return plantasRecuperables;
    }

    public List<String> getObservaciones() { return observaciones; }
}