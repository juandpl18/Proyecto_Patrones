package com.smartgrid.smartgrid.repository;

import com.smartgrid.smartgrid.model.Medidor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedidorRepository extends JpaRepository<Medidor, Long> {

    Optional<Medidor> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Medidor> findByClienteId(Long clienteId);
}