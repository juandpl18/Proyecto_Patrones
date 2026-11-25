package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.builder.PlantaEnergia;
import com.smartgrid.smartgrid.model.EstadoPlanta;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.TipoFuente;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlantaGeneracionService {

    private final PlantaGeneracionRepository repository;

    public PlantaGeneracionService(PlantaGeneracionRepository repository) {
        this.repository = repository;
    }

    /** Persiste la planta que construyó el Builder. */
    @Transactional
    public PlantaGeneracion registrar(PlantaEnergia planta, String creadoPor) {

        String nombre = planta.getNombre().trim();

        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una planta con el nombre: " + nombre);
        }

        PlantaGeneracion entidad = new PlantaGeneracion();
        entidad.setNombre(nombre);
        // El Flyweight ya normaliza el tipo en mayúsculas (SOLAR, EOLICA...)
        entidad.setTipoFuente(
                TipoFuente.valueOf(planta.getFuenteCompartida().getTipo()));
        entidad.setCapacidadKw(BigDecimal.valueOf(planta.getCapacidadKw()));
        entidad.setVoltajeSalida(BigDecimal.valueOf(planta.getVoltajeSalida()));
        entidad.setTieneBateriaReserva(planta.isTieneBateriaReserva());
        entidad.setUbicacion(planta.getUbicacion());
        entidad.setEstado(EstadoPlanta.ACTIVA);
        entidad.setCreadoPor(creadoPor);

        return repository.save(entidad);
    }

    @Transactional(readOnly = true)
    public List<PlantaGeneracion> listar() {
        return repository.findAll();
    }
}