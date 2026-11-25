package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.composite.ComponenteRed;
import com.smartgrid.smartgrid.composite.ZonaCompuesta;
import com.smartgrid.smartgrid.composite.ZonaHoja;
import com.smartgrid.smartgrid.dto.ConsumoResponse;
import com.smartgrid.smartgrid.dto.NodoConsumoResponse;
import com.smartgrid.smartgrid.dto.Periodo;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.Medidor;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.ClienteRepository;
import com.smartgrid.smartgrid.repository.ConsumoPorZona;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import com.smartgrid.smartgrid.repository.MedidorRepository;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ConsumoService {

    private final LecturaConsumoRepository lecturaRepository;
    private final MedidorRepository medidorRepository;
    private final ClienteRepository clienteRepository;
    private final ZonaRedRepository zonaRepository;

    public ConsumoService(
            LecturaConsumoRepository lecturaRepository,
            MedidorRepository medidorRepository,
            ClienteRepository clienteRepository,
            ZonaRedRepository zonaRepository) {

        this.lecturaRepository = lecturaRepository;
        this.medidorRepository = medidorRepository;
        this.clienteRepository = clienteRepository;
        this.zonaRepository = zonaRepository;
    }

    public ConsumoResponse porMedidor(Long medidorId, Periodo periodo) {

        Medidor medidor = medidorRepository.findById(medidorId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Medidor no encontrado: " + medidorId));

        BigDecimal total = valorOCero(lecturaRepository.sumaPorMedidor(
                medidorId, periodo.desde(), periodo.hasta()));

        return new ConsumoResponse("MEDIDOR", medidorId, medidor.getCodigo(),
                periodo.desde(), periodo.hasta(), total);
    }

    public ConsumoResponse porCliente(Long clienteId, Periodo periodo) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cliente no encontrado: " + clienteId));

        BigDecimal total = valorOCero(lecturaRepository.sumaPorCliente(
                clienteId, periodo.desde(), periodo.hasta()));

        return new ConsumoResponse("CLIENTE", clienteId, cliente.getNombre(),
                periodo.desde(), periodo.hasta(), total);
    }

    /** Arma el árbol Composite de la red con el consumo de cada nodo. */
    public List<NodoConsumoResponse> arbol(Periodo periodo) {

        List<ZonaRed> zonas = zonaRepository.findAll();

        Map<Long, BigDecimal> consumoDirecto = lecturaRepository
                .consumoPorZona(periodo.desde(), periodo.hasta())
                .stream()
                .collect(Collectors.toMap(
                        ConsumoPorZona::getZonaId,
                        ConsumoPorZona::getTotal));

        Map<Long, List<ZonaRed>> hijosPorPadre = zonas.stream()
                .filter(z -> z.getZonaPadre() != null)
                .collect(Collectors.groupingBy(z -> z.getZonaPadre().getId()));

        return zonas.stream()
                .filter(z -> z.getZonaPadre() == null)
                .map(raiz -> construir(raiz, hijosPorPadre, consumoDirecto))
                .map(NodoConsumoResponse::desde)
                .toList();
    }

    private ComponenteRed construir(
            ZonaRed zona,
            Map<Long, List<ZonaRed>> hijosPorPadre,
            Map<Long, BigDecimal> consumoDirecto) {

        BigDecimal propio =
                consumoDirecto.getOrDefault(zona.getId(), BigDecimal.ZERO);
        List<ZonaRed> hijas =
                hijosPorPadre.getOrDefault(zona.getId(), List.of());

        if (hijas.isEmpty()) {
            return new ZonaHoja(zona.getId(), zona.getNombre(),
                    zona.getTipo().name(), zona.getCapacidadKw(), propio);
        }

        ZonaCompuesta compuesta = new ZonaCompuesta(zona.getId(),
                zona.getNombre(), zona.getTipo().name(),
                zona.getCapacidadKw(), propio);

        hijas.forEach(hija ->
                compuesta.agregar(construir(hija, hijosPorPadre, consumoDirecto)));

        return compuesta;
    }

    private BigDecimal valorOCero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}