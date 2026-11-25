package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.FacturaResponse;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.EstadoFactura;
import com.smartgrid.smartgrid.model.FacturaEmitida;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.template.FacturaCalculada;
import com.smartgrid.smartgrid.template.PlantillaFacturacionRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FacturaService {

    private static final int MOTIVO_MAXIMO = 200;

    private static final Map<EstadoFactura, Set<EstadoFactura>> TRANSICIONES =
            Map.of(
                    EstadoFactura.EMITIDA, Set.of(EstadoFactura.PAGADA,
                            EstadoFactura.VENCIDA, EstadoFactura.ANULADA),
                    EstadoFactura.VENCIDA, Set.of(EstadoFactura.PAGADA,
                            EstadoFactura.ANULADA),
                    EstadoFactura.PAGADA, Set.of(),
                    EstadoFactura.ANULADA, Set.of());

    private final FacturaEmitidaRepository facturaRepository;
    private final FacturaDetalleRepository detalleRepository;
    private final ClienteRepository clienteRepository;
    private final TarifaService tarifaService;
    private final PlantillaFacturacionRegistry plantillas;
    private final AutorizacionService autorizacionService;
    private final ZoneId zona;

    public FacturaService(
            FacturaEmitidaRepository facturaRepository,
            FacturaDetalleRepository detalleRepository,
            ClienteRepository clienteRepository,
            TarifaService tarifaService,
            PlantillaFacturacionRegistry plantillas,
            AutorizacionService autorizacionService,
            @Value("${smartgrid.facturacion.zona-horaria:America/Bogota}")
            String zona) {

        this.facturaRepository = facturaRepository;
        this.detalleRepository = detalleRepository;
        this.clienteRepository = clienteRepository;
        this.tarifaService = tarifaService;
        this.plantillas = plantillas;
        this.autorizacionService = autorizacionService;
        this.zona = ZoneId.of(zona);
    }

    @Transactional
    public FacturaResponse emitir(
            Usuario solicitante,
            Long clienteId,
            LocalDate desde,
            LocalDate hasta,
            String modo) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        FacturaCalculada calculada = plantillas.para(modo).ejecutar(
                buscarCliente(clienteId), desde, hasta, null,
                solicitante.getUsername(), true);

        return FacturaResponse.desdeCalculo(calculada);
    }

    /** Calcula la factura sin guardarla. Con tarifaId compara otra tarifa. */
    @Transactional(readOnly = true)
    public FacturaResponse simular(
            Long clienteId,
            LocalDate desde,
            LocalDate hasta,
            String modo,
            Long tarifaId) {

        Tarifa tarifa = tarifaId != null ? tarifaService.buscar(tarifaId) : null;

        FacturaCalculada calculada = plantillas.para(modo).ejecutar(
                buscarCliente(clienteId), desde, hasta, tarifa, null, false);

        return FacturaResponse.desdeCalculo(calculada);
    }

    @Transactional(readOnly = true)
    public FacturaResponse obtener(Long id) {
        FacturaEmitida factura = buscar(id);
        return FacturaResponse.desdeEntidad(factura,
                detalleRepository.findByFacturaIdOrderByOrden(id));
    }

    /** Listado resumido (sin líneas); el detalle se consulta por id. */
    @Transactional(readOnly = true)
    public List<FacturaResponse> listar(Long clienteId, String estado) {

        EstadoFactura filtro = estado == null || estado.isBlank()
                ? null
                : EnumParser.parsear(EstadoFactura.class, estado, "estado");

        List<FacturaEmitida> facturas;
        if (clienteId != null && filtro != null) {
            facturas = facturaRepository
                    .findByClienteIdAndEstadoOrderByEmitidaEnDesc(
                            clienteId, filtro);
        } else if (clienteId != null) {
            facturas = facturaRepository
                    .findByClienteIdOrderByEmitidaEnDesc(clienteId);
        } else if (filtro != null) {
            facturas = facturaRepository
                    .findByEstadoOrderByEmitidaEnDesc(filtro);
        } else {
            facturas = facturaRepository.findAllByOrderByEmitidaEnDesc();
        }

        return facturas.stream()
                .map(f -> FacturaResponse.desdeEntidad(f, List.of()))
                .toList();
    }

    @Transactional
    public FacturaResponse pagar(Usuario solicitante, Long id) {

        autorizacionService.validarAlgunRol(
                solicitante, Rol.ADMIN, Rol.OPERADOR);

        FacturaEmitida factura = buscar(id);
        validarTransicion(factura, EstadoFactura.PAGADA);

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setPagadaEn(Instant.now());
        facturaRepository.save(factura);

        return respuesta(factura);
    }

    @Transactional
    public FacturaResponse anular(Usuario solicitante, Long id, String motivo) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (motivo == null || motivo.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El motivo de anulación es obligatorio");
        }
        if (motivo.length() > MOTIVO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El motivo no puede superar " + MOTIVO_MAXIMO
                            + " caracteres");
        }

        FacturaEmitida factura = buscar(id);
        validarTransicion(factura, EstadoFactura.ANULADA);

        factura.setEstado(EstadoFactura.ANULADA);
        factura.setAnuladaEn(Instant.now());
        factura.setMotivoAnulacion(motivo.trim());
        facturaRepository.save(factura);

        return respuesta(factura);
    }

    /** Pasa a VENCIDA las facturas EMITIDAS cuyo vencimiento ya pasó. */
    @Transactional
    public Map<String, Integer> marcarVencidas(Usuario solicitante) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        List<FacturaEmitida> vencidas = facturaRepository
                .findByEstadoAndFechaVencimientoBefore(
                        EstadoFactura.EMITIDA, LocalDate.now(zona));

        vencidas.forEach(f -> f.setEstado(EstadoFactura.VENCIDA));
        facturaRepository.saveAll(vencidas);

        return Map.of("marcadas", vencidas.size());
    }

    private void validarTransicion(FacturaEmitida factura, EstadoFactura destino) {
        EstadoFactura actual = factura.getEstado();

        if (!TRANSICIONES.get(actual).contains(destino)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede pasar una factura de " + actual
                            + " a " + destino);
        }
    }

    private FacturaResponse respuesta(FacturaEmitida factura) {
        return FacturaResponse.desdeEntidad(factura,
                detalleRepository.findByFacturaIdOrderByOrden(factura.getId()));
    }

    private FacturaEmitida buscar(Long id) {
        return facturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Factura no encontrada: " + id));
    }

    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cliente no encontrado: " + id));
    }
}