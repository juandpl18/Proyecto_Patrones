package com.smartgrid.smartgrid.template;

import com.smartgrid.smartgrid.facturacion.ConstructorCalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.LecturaHora;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.ModoFacturacion;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.repository.LecturaConsumoRepository;
import com.smartgrid.smartgrid.service.TarifaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/** Modo normal: el consumo sale de las lecturas reales del período. */
@Component
public class FacturacionPorLecturas extends PlantillaFacturacion {

    private final LecturaConsumoRepository lecturaRepository;

    public FacturacionPorLecturas(
            TarifaService tarifaService,
            ConstructorCalculoFactura constructorCalculo,
            FacturaEmitidaRepository facturaRepository,
            FacturaDetalleRepository detalleRepository,
            LecturaConsumoRepository lecturaRepository,
            @Value("${smartgrid.facturacion.zona-horaria:America/Bogota}")
            String zona) {

        super(tarifaService, constructorCalculo, facturaRepository,
                detalleRepository, ZoneId.of(zona));
        this.lecturaRepository = lecturaRepository;
    }

    @Override
    public ModoFacturacion modo() {
        return ModoFacturacion.LECTURAS;
    }

    @Override
    protected DatosFacturacion obtenerConsumo(
            Cliente cliente, LocalDate desde, LocalDate hasta) {

        // [desde 00:00, hasta+1 00:00) en la hora local
        Instant inicio = desde.atStartOfDay(zona()).toInstant();
        Instant fin = hasta.plusDays(1).atStartOfDay(zona()).toInstant();

        List<LecturaHora> lecturas = lecturaRepository
                .lecturasClienteEnRango(cliente.getId(), inicio, fin)
                .stream()
                .map(l -> new LecturaHora(l.getFechaHora(), l.getConsumoKwh()))
                .toList();

        if (lecturas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El cliente no tiene lecturas en el período; "
                            + "use modo=ESTIMADA para facturar por promedio");
        }

        BigDecimal total = lecturas.stream()
                .map(LecturaHora::kwh)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DatosFacturacion(cliente.getTipo(), total, lecturas);
    }
}