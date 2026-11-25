package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.FacturaResponse;
import com.smartgrid.smartgrid.dto.LoteResponse;
import com.smartgrid.smartgrid.dto.LoteResponse.Emitida;
import com.smartgrid.smartgrid.dto.LoteResponse.Omitida;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import com.smartgrid.smartgrid.template.PlantillaFacturacionRegistry;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FacturaLoteService {

    private final ClienteRepository clienteRepository;
    private final FacturaService facturaService;
    private final PlantillaFacturacionRegistry plantillas;
    private final AutorizacionService autorizacionService;

    public FacturaLoteService(
            ClienteRepository clienteRepository,
            FacturaService facturaService,
            PlantillaFacturacionRegistry plantillas,
            AutorizacionService autorizacionService) {

        this.clienteRepository = clienteRepository;
        this.facturaService = facturaService;
        this.plantillas = plantillas;
        this.autorizacionService = autorizacionService;
    }

    /** Factura a todos los clientes activos; informa a quiénes se omitió y por qué. */
    public LoteResponse emitirLote(
            Usuario solicitante, LocalDate desde, LocalDate hasta, String modo) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);
        plantillas.para(modo); // falla una sola vez si el modo es inválido

        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(Cliente::isActivo)
                .toList();

        List<Emitida> emitidas = new ArrayList<>();
        List<Omitida> omitidas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Cliente cliente : clientes) {
            try {
                FacturaResponse factura = facturaService.emitir(
                        solicitante, cliente.getId(), desde, hasta, modo);

                emitidas.add(new Emitida(cliente.getId(), cliente.getNombre(),
                        factura.id(), factura.total()));
                total = total.add(factura.total());

            } catch (ResponseStatusException | IllegalArgumentException
                     | DataIntegrityViolationException e) {
                omitidas.add(new Omitida(cliente.getId(), cliente.getNombre(),
                        mensaje(e)));
            }
        }

        return new LoteResponse(
                desde, hasta,
                modo == null || modo.isBlank() ? "LECTURAS" : modo.toUpperCase(),
                clientes.size(), emitidas.size(), omitidas.size(),
                total, emitidas, omitidas);
    }

    private String mensaje(RuntimeException e) {
        if (e instanceof ResponseStatusException r && r.getReason() != null) {
            return r.getReason();
        }
        return e.getMessage();
    }
}