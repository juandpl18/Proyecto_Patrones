package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.ClienteResponse;
import com.smartgrid.smartgrid.dto.CrearClienteRequest;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AutorizacionService autorizacionService;

    public ClienteService(
            ClienteRepository clienteRepository,
            AutorizacionService autorizacionService) {

        this.clienteRepository = clienteRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional
    public ClienteResponse crear(Usuario solicitante, CrearClienteRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (datos == null || datos.documento() == null
                || datos.documento().isBlank()
                || datos.nombre() == null || datos.nombre().isBlank()
                || datos.tipo() == null || datos.tipo().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "documento, nombre y tipo son obligatorios");
        }

        TipoCliente tipo;
        try {
            tipo = TipoCliente.valueOf(datos.tipo().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo inválido. Opciones: RESIDENCIAL o COMERCIAL");
        }

        String documento = datos.documento().trim();
        if (clienteRepository.existsByDocumento(documento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cliente con el documento: " + documento);
        }

        Cliente cliente = new Cliente();
        cliente.setDocumento(documento);
        cliente.setNombre(datos.nombre().trim());
        cliente.setTipo(tipo);
        cliente.setDireccion(datos.direccion());

        return ClienteResponse.desde(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream()
                .map(ClienteResponse::desde)
                .toList();
    }
}