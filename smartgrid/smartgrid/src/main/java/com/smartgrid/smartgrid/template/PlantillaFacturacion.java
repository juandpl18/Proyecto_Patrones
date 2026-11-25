package com.smartgrid.smartgrid.template;

import com.smartgrid.smartgrid.facturacion.ConstructorCalculoFactura;
import com.smartgrid.smartgrid.facturacion.DatosFacturacion;
import com.smartgrid.smartgrid.facturacion.LineaFactura;
import com.smartgrid.smartgrid.facturacion.ResultadoCalculo;
import com.smartgrid.smartgrid.model.Cliente;
import com.smartgrid.smartgrid.model.EstadoFactura;
import com.smartgrid.smartgrid.model.FacturaDetalle;
import com.smartgrid.smartgrid.model.FacturaEmitida;
import com.smartgrid.smartgrid.model.ModoFacturacion;
import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.repository.FacturaDetalleRepository;
import com.smartgrid.smartgrid.repository.FacturaEmitidaRepository;
import com.smartgrid.smartgrid.service.TarifaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Template Method: el ciclo de facturación es fijo (método ejecutar) y las
 * subclases solo definen cómo se obtiene el consumo.
 */
public abstract class PlantillaFacturacion {

    private static final int DIAS_VENCIMIENTO = 15;
    private static final int DIAS_MAXIMOS_PERIODO = 366;

    private final TarifaService tarifaService;
    private final ConstructorCalculoFactura constructorCalculo;
    private final FacturaEmitidaRepository facturaRepository;
    private final FacturaDetalleRepository detalleRepository;
    private final ZoneId zona;

    protected PlantillaFacturacion(
            TarifaService tarifaService,
            ConstructorCalculoFactura constructorCalculo,
            FacturaEmitidaRepository facturaRepository,
            FacturaDetalleRepository detalleRepository,
            ZoneId zona) {

        this.tarifaService = tarifaService;
        this.constructorCalculo = constructorCalculo;
        this.facturaRepository = facturaRepository;
        this.detalleRepository = detalleRepository;
        this.zona = zona;
    }

    public abstract ModoFacturacion modo();

    /** Paso variable: de dónde sale el consumo a facturar. */
    protected abstract DatosFacturacion obtenerConsumo(
            Cliente cliente, LocalDate desde, LocalDate hasta);

    /** Gancho opcional: se ejecuta después de guardar la factura. */
    protected void despuesDeEmitir(FacturaEmitida factura) {
    }

    /** El algoritmo: los pasos y su orden no cambian entre subclases. */
    public final FacturaCalculada ejecutar(
            Cliente cliente,
            LocalDate desde,
            LocalDate hasta,
            Tarifa tarifaForzada,
            String usuario,
            boolean guardar) {

        validarCliente(cliente);
        validarPeriodo(desde, hasta);

        DatosFacturacion datos = obtenerConsumo(cliente, desde, hasta);
        Tarifa tarifa = seleccionarTarifa(cliente, hasta, tarifaForzada);
        ResultadoCalculo resultado = calcular(cliente, tarifa, datos);

        FacturaCalculada calculada = new FacturaCalculada(
                null, cliente, tarifa, modo(), desde, hasta,
                datos.consumoKwh(), resultado,
                LocalDate.now(zona).plusDays(DIAS_VENCIMIENTO),
                "SIMULADA", null);

        if (!guardar) {
            return calculada;
        }

        FacturaEmitida emitida = persistir(calculada, usuario);
        despuesDeEmitir(emitida);

        return calculada.emitida(emitida.getId(), emitida.getEmitidaEn());
    }

    protected FacturaEmitidaRepository facturaRepository() {
        return facturaRepository;
    }

    protected ZoneId zona() {
        return zona;
    }

    private void validarCliente(Cliente cliente) {
        if (!cliente.isActivo()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El cliente está inactivo y no se puede facturar");
        }
    }

    private void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "desde y hasta son obligatorios");
        }
        if (hasta.isBefore(desde)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "'hasta' no puede ser anterior a 'desde'");
        }
        if (ChronoUnit.DAYS.between(desde, hasta) > DIAS_MAXIMOS_PERIODO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El período no puede superar " + DIAS_MAXIMOS_PERIODO
                            + " días");
        }
        if (hasta.isAfter(LocalDate.now(zona))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El período no puede terminar en el futuro");
        }
    }

    private Tarifa seleccionarTarifa(
            Cliente cliente, LocalDate hasta, Tarifa tarifaForzada) {

        if (tarifaForzada == null) {
            return tarifaService.vigente(cliente.getTipo(), hasta);
        }

        if (tarifaForzada.getTipoCliente() != cliente.getTipo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La tarifa indicada es para clientes "
                            + tarifaForzada.getTipoCliente()
                            + " y el cliente es " + cliente.getTipo());
        }
        return tarifaForzada;
    }

    private ResultadoCalculo calcular(
            Cliente cliente, Tarifa tarifa, DatosFacturacion datos) {

        return constructorCalculo
                .construir(tarifaService.configurar(tarifa), cliente.getTipo())
                .calcular(datos);
    }

    private FacturaEmitida persistir(FacturaCalculada c, String usuario) {

        boolean duplicada = facturaRepository
                .existsByClienteIdAndPeriodoInicioAndPeriodoFinAndEstadoNot(
                        c.cliente().getId(), c.desde(), c.hasta(),
                        EstadoFactura.ANULADA);

        if (duplicada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El cliente ya tiene una factura vigente para el período "
                            + c.desde() + " a " + c.hasta());
        }

        FacturaEmitida factura = new FacturaEmitida();
        factura.setCliente(c.cliente());
        factura.setTarifa(c.tarifa());
        factura.setPeriodoInicio(c.desde());
        factura.setPeriodoFin(c.hasta());
        factura.setConsumoKwh(c.consumoKwh().setScale(3, RoundingMode.HALF_UP));
        factura.setPrecioKwh(c.precioPromedioKwh());
        factura.setEnergia(c.resultado().energia());
        factura.setTotal(c.resultado().total());
        factura.setEstado(EstadoFactura.EMITIDA);
        factura.setModo(c.modo());
        factura.setFechaVencimiento(c.vencimiento());
        factura.setEmitidaPor(usuario);

        FacturaEmitida guardada = facturaRepository.save(factura);

        List<FacturaDetalle> detalle = new ArrayList<>();
        int orden = 1;
        for (LineaFactura linea : c.resultado().lineas()) {
            FacturaDetalle fila = new FacturaDetalle();
            fila.setFactura(guardada);
            fila.setOrden(orden++);
            fila.setConcepto(linea.concepto());
            fila.setValor(linea.valor());
            detalle.add(fila);
        }
        detalleRepository.saveAll(detalle);

        return guardada;
    }
}