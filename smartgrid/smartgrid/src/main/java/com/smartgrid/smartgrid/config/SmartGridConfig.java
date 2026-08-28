package com.smartgrid.smartgrid.config;

import org.springframework.stereotype.Component;

@Component
public class SmartGridConfig {

    private String nombreSistema = "SmartGrid";
    private boolean sistemaActivo = true;

    public String getNombreSistema() {
        return nombreSistema;
    }

    public boolean isSistemaActivo() {
        return sistemaActivo;
    }

    public void activarSistema() {
        sistemaActivo = true;
    }

    public void desactivarSistema() {
        sistemaActivo = false;
    }
}
