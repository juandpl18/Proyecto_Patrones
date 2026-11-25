package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.AlertaResponse;
import com.smartgrid.smartgrid.model.Alerta;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.AlertaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final AutorizacionService autorizacionService;

    public AlertaService(
            AlertaRepository alertaRepository,
            AutorizacionService autorizacionService) {

        this.alertaRepository = alertaRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional(readOnly = true)
    public List<AlertaResponse> listarPendientes() {
        return alertaRepository.findByAtendidaFalseOrderByCreadaEnDesc()
                .stream()
                .map(AlertaResponse::desde)
                .toList();
    }

    @Transactional
    public AlertaResponse atender(Usuario solicitante, Long id) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        Alerta alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Alerta no encontrada: " + id));

        alerta.setAtendida(true);
        return AlertaResponse.desde(alertaRepository.save(alerta));
    }
}