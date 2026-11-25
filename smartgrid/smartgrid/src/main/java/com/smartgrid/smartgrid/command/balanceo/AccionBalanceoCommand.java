package com.smartgrid.smartgrid.command.balanceo;

public interface AccionBalanceoCommand {

    void ejecutar();

    void deshacer();

    String descripcion();
}