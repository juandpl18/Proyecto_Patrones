package com.smartgrid.smartgrid.template;

import com.smartgrid.smartgrid.facturacion.ConstructorCalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.EstadoFactura;
import com.smartgrid.smartgrid.model.FacturaEmitida;
import com.smartgrid.smartgrid.model.ModoFacturacion;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.service.TarifaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/** Sin lecturas: el consumo es el promedio de las últimas 3 facturas no anuladas. */
@Component
public class FacturacionEstimada extends PlantillaFacturacion {

    private static final Logger log =
            LoggerFactory.getLogger(FacturacionEstimada.class);

    public FacturacionEstimada(
            TarifaService tarifaService,
            ConstructorCalculoFactura constructorCalculo,
            FacturaEmitidaRepository facturaRepository,
            FacturaDetalleRepository detalleRepository,
            @Value("${smartgrid.facturacion.zona-horaria:America/Bogota}")
            String zona) {

        super(tarifaService, constructorCalculo, facturaRepository,
                detalleRepository, ZoneId.of(zona));
    }

    @Override
    public ModoFacturacion modo() {
        return ModoFacturacion.ESTIMADA;
    }

    @Override
    protected DatosFacturacion obtenerConsumo(
            Cliente cliente, LocalDate desde, LocalDate hasta) {

        List<FacturaEmitida> previas = facturaRepository()
                .findTop3ByClienteIdAndEstadoNotOrderByPeriodoFinDesc(
                        cliente.getId(), EstadoFactura.ANULADA);

        if (previas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El cliente no tiene facturas previas para estimar "
                            + "su consumo");
        }

        BigDecimal promedio = previas.stream()
                .map(FacturaEmitida::getConsumoKwh)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(previas.size()), 3,
                        RoundingMode.HALF_UP);

        return new DatosFacturacion(cliente.getTipo(), promedio, List.of());
    }

    @Override
    protected void despuesDeEmitir(FacturaEmitida factura) {
        log.info("Factura estimada emitida para el cliente {}",
                factura.getCliente().getId());
    }
}