package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.TipoFuente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlantaGeneracionRepository
        extends JpaRepository<PlantaGeneracion, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<PlantaGeneracion> findByTipoFuente(TipoFuente tipoFuente);
}