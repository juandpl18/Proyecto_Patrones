package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.LecturaResponse;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.dto.RegistrarLecturaRequest;
import com.smartgrid.smartgrid.model.LecturaConsumo;
import com.smartgrid.smartgrid.model.Medidor;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.observer.LecturaConsumoSubject;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import com.smartgrid.smartgrid.repository.MedidorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LecturaService {

    private final LecturaConsumoRepository lecturaRepository;
    private final MedidorRepository medidorRepository;
    private final AutorizacionService autorizacionService;
    private final LecturaConsumoSubject subject;

    public LecturaService(
            LecturaConsumoRepository lecturaRepository,
            MedidorRepository medidorRepository,
            AutorizacionService autorizacionService,
            LecturaConsumoSubject subject) {

        this.lecturaRepository = lecturaRepository;
        this.medidorRepository = medidorRepository;
        this.autorizacionService = autorizacionService;
        this.subject = subject;
    }

    @Transactional
    public LecturaResponse registrar(
            Usuario solicitante, RegistrarLecturaRequest datos) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        if (datos == null || datos.codigoMedidor() == null
                || datos.codigoMedidor().isBlank()
                || datos.consumoKwh() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "codigoMedidor y consumoKwh son obligatorios");
        }

        if (datos.consumoKwh().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El consumo no puede ser negativo");
        }

        Instant fecha = datos.fechaHora() != null
                ? datos.fechaHora() : Instant.now();

        if (fecha.isAfter(Instant.now().plus(5, ChronoUnit.MINUTES))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La fecha de la lectura no puede estar en el futuro");
        }

        Medidor medidor = medidorRepository
                .findByCodigo(datos.codigoMedidor().trim())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medidor no encontrado: " + datos.codigoMedidor()));

        if (!medidor.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El medidor está inactivo");
        }

        if (lecturaRepository.existsByMedidorIdAndFechaHora(
                medidor.getId(), fecha)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe una lectura de ese medidor en esa fecha y hora");
        }

        LecturaConsumo lectura = new LecturaConsumo();
        lectura.setMedidor(medidor);
        lectura.setFechaHora(fecha);
        lectura.setConsumoKwh(datos.consumoKwh());

        LecturaConsumo guardada = lecturaRepository.save(lectura);

        // Observer: avisa a quienes reaccionan a nuevas lecturas (alertas, etc.)
        subject.notificar(guardada);

        return LecturaResponse.desde(guardada);
    }

    @Transactional(readOnly = true)
    public List<LecturaResponse> listar(Long medidorId, Periodo periodo) {

        if (!medidorRepository.existsById(medidorId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Medidor no encontrado: " + medidorId);
        }

        return lecturaRepository
                .findByMedidorIdAndFechaHoraBetweenOrderByFechaHora(
                        medidorId, periodo.desde(), periodo.hasta())
                .stream()
                .map(LecturaResponse::desde)
                .toList();
    }
}