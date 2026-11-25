package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.CargoAdicional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CargoAdicionalRepository
        extends JpaRepository<CargoAdicional, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    List<CargoAdicional> findByActivoTrueOrderById();

    List<CargoAdicional> findAllByOrderById();
}