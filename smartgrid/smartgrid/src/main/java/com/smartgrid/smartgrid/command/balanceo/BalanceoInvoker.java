package com.smartgrid.smartgrid.command.balanceo;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BalanceoInvoker {

    public void ejecutar(List<AccionBalanceoCommand> comandos) {
        comandos.forEach(AccionBalanceoCommand::ejecutar);
    }

    /** Deshace en orden inverso al de ejecución. */
    public void deshacer(List<AccionBalanceoCommand> comandos) {
        for (int i = comandos.size() - 1; i >= 0; i--) {
            comandos.get(i).deshacer();
        }
    }
}