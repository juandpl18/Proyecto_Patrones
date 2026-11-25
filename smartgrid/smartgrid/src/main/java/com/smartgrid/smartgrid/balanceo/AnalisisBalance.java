package com.smartgrid.smartgrid.balanceo;

import com.smartgrid.smartgrid.model.ZonaRed;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Resultado de analizar las zonas hijas de una zona en un instante. */
public record AnalisisBalance(
        ZonaRed alcance,
        Instant desde,
        Instant hasta,
        List<ZonaAnalizada> zonas) {

    public Map<Long, ZonaAnalizada> porId() {
        Map<Long, ZonaAnalizada> mapa = new LinkedHashMap<>();
        zonas.forEach(z -> mapa.put(z.zona().getId(), z));
        return mapa;
    }
}