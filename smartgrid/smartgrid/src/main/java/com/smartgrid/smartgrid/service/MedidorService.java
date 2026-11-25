package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CrearMedidorRequest;
import com.smartgrid.smartgrid.dto.MedidorResponse;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.Medidor;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import com.smartgrid.smartgrid.repository.MedidorRepository;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MedidorService {

    private final MedidorRepository medidorRepository;
    private final ClienteRepository clienteRepository;
    private final ZonaRedRepository zonaRepository;
    private final AutorizacionService autorizacionService;

    public MedidorService(
            MedidorRepository medidorRepository,
            ClienteRepository clienteRepository,
            ZonaRedRepository zonaRepository,
            AutorizacionService autorizacionService) {

        this.medidorRepository = medidorRepository;
        this.clienteRepository = clienteRepository;
        this.zonaRepository = zonaRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional
    public MedidorResponse crear(Usuario solicitante, CrearMedidorRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (datos == null || datos.codigo() == null || datos.codigo().isBlank()
                || datos.clienteId() == null || datos.zonaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "codigo, clienteId y zonaId son obligatorios");
        }

        String codigo = datos.codigo().trim();
        if (medidorRepository.existsByCodigo(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un medidor con el código: " + codigo);
        }

        Cliente cliente = clienteRepository.findById(datos.clienteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado: " + datos.clienteId()));

        ZonaRed zona = zonaRepository.findById(datos.zonaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Zona no encontrada: " + datos.zonaId()));

        Medidor medidor = new Medidor();
        medidor.setCodigo(codigo);
        medidor.setCliente(cliente);
        medidor.setZona(zona);

        return MedidorResponse.desde(medidorRepository.save(medidor));
    }

    @Transactional(readOnly = true)
    public List<MedidorResponse> listar() {
        return medidorRepository.findAll().stream()
                .map(MedidorResponse::desde)
                .toList();
    }
}