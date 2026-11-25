package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.CambioEstadoPlanta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CambioEstadoPlantaRepository
        extends JpaRepository<CambioEstadoPlanta, Long> {

    List<CambioEstadoPlanta> findByPlantaIdOrderByFechaHoraDesc(Long plantaId);
}