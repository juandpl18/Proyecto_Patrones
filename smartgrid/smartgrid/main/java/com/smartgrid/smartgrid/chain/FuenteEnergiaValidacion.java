package com.smartgrid.smartgrid.chain;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class FuenteEnergiaValidacion extends ValidacionHandler {

    private static final Set<String> FUENTES_VALIDAS =
            Set.of("SOLAR", "EOLICA", "HIDRAULICA");

    @Override
    protected void procesarValidacion(
            String nombre,
            String fuente,
            Double capacidad) {

        if (fuente == null ||
                !FUENTES_VALIDAS.contains(
                        fuente.trim().toUpperCase())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fuente de energía no es válida. "
                            + "Opciones: SOLAR, EOLICA o HIDRAULICA"
            );
        }
    }
}