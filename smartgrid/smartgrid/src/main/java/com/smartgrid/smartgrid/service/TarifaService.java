package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CrearTarifaRequest;
import com.smartgrid.smartgrid.dto.CrearTarifaRequest.FranjaRequest;
import com.smartgrid.smartgrid.dto.CrearTarifaRequest.TramoRequest;
import com.smartgrid.smartgrid.dto.TarifaResponse;
import com.smartgrid.smartgrid.model.ModalidadTarifa;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Tarifa;
import com.smartgrid.smartgrid.model.TarifaFranja;
import com.smartgrid.smartgrid.model.TarifaTramo;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.TarifaFranjaRepository;
import com.smartgrid.smartgrid.repository.TarifaRepository;
import com.smartgrid.smartgrid.repository.TarifaTramoRepository;
import com.smartgrid.smartgrid.strategy.tarifa.TarifaConfig;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TarifaTramoRepository tramoRepository;
    private final TarifaFranjaRepository franjaRepository;
    private final AutorizacionService autorizacionService;

    public TarifaService(
            TarifaRepository tarifaRepository,
            TarifaTramoRepository tramoRepository,
            TarifaFranjaRepository franjaRepository,
            AutorizacionService autorizacionService) {

        this.tarifaRepository = tarifaRepository;
        this.tramoRepository = tramoRepository;
        this.franjaRepository = franjaRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional
    public TarifaResponse crear(Usuario solicitante, CrearTarifaRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (datos == null || esVacio(datos.nombre())
                || esVacio(datos.tipoCliente()) || esVacio(datos.modalidad())
                || datos.vigenteDesde() == null) {
            throw error(HttpStatus.BAD_REQUEST,
                    "nombre, tipoCliente, modalidad y vigenteDesde "
                            + "son obligatorios");
        }

        TipoCliente tipo = EnumParser.parsear(
                TipoCliente.class, datos.tipoCliente(), "tipoCliente");
        ModalidadTarifa modalidad = EnumParser.parsear(
                ModalidadTarifa.class, datos.modalidad(), "modalidad");

        List<TramoRequest> tramos =
                datos.tramos() == null ? List.of() : datos.tramos();
        List<FranjaRequest> franjas =
                datos.franjas() == null ? List.of() : datos.franjas();

        switch (modalidad) {
            case PLANA -> validarPlana(datos.precioKwh(), tramos, franjas);
            case ESCALONADA -> validarEscalonada(tramos, franjas);
            case HORARIA -> validarHoraria(franjas, tramos);
        }

        cerrarTarifaAnterior(tipo, datos.vigenteDesde());

        Tarifa tarifa = new Tarifa();
        tarifa.setTipoCliente(tipo);
        tarifa.setNombre(datos.nombre().trim());
        tarifa.setModalidad(modalidad);
        tarifa.setPrecioKwh(modalidad == ModalidadTarifa.PLANA
                ? datos.precioKwh().setScale(2, RoundingMode.HALF_UP) : null);
        tarifa.setVigenteDesde(datos.vigenteDesde());
        tarifa.setActiva(true);
        tarifa = tarifaRepository.save(tarifa);

        List<TarifaTramo> tramosGuardados = new ArrayList<>();
        for (TramoRequest t : ordenarTramos(tramos)) {
            TarifaTramo tramo = new TarifaTramo();
            tramo.setTarifa(tarifa);
            tramo.setDesdeKwh(t.desdeKwh());
            tramo.setHastaKwh(t.hastaKwh());
            tramo.setPrecioKwh(t.precioKwh());
            tramosGuardados.add(tramo);
        }
        tramoRepository.saveAll(tramosGuardados);

        List<TarifaFranja> franjasGuardadas = new ArrayList<>();
        for (FranjaRequest f : franjas) {
            TarifaFranja franja = new TarifaFranja();
            franja.setTarifa(tarifa);
            franja.setNombre(f.nombre().trim());
            franja.setHoraInicio(f.horaInicio());
            franja.setHoraFin(f.horaFin());
            franja.setPrecioKwh(f.precioKwh());
            franjasGuardadas.add(franja);
        }
        franjaRepository.saveAll(franjasGuardadas);

        return TarifaResponse.desde(tarifa, tramosGuardados, franjasGuardadas);
    }

    @Transactional(readOnly = true)
    public List<TarifaResponse> listar() {
        return tarifaRepository.findAll().stream()
                .sorted(Comparator
                        .comparing((Tarifa t) -> t.getTipoCliente().name())
                        .thenComparing(Tarifa::getVigenteDesde,
                                Comparator.reverseOrder()))
                .map(this::responder)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarifaResponse vigenteResponse(TipoCliente tipo, LocalDate fecha) {
        return responder(vigente(tipo, fecha));
    }

    /** Tarifa vigente de un tipo de cliente en una fecha. */
    @Transactional(readOnly = true)
    public Tarifa vigente(TipoCliente tipo, LocalDate fecha) {
        List<Tarifa> vigentes = tarifaRepository.vigentesEn(tipo, fecha);

        if (vigentes.isEmpty()) {
            throw error(HttpStatus.CONFLICT,
                    "No hay tarifa vigente para clientes " + tipo
                            + " el " + fecha);
        }
        return vigentes.get(0);
    }

    @Transactional(readOnly = true)
    public Tarifa buscar(Long id) {
        return tarifaRepository.findById(id)
                .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                        "Tarifa no encontrada: " + id));
    }

    /** Convierte una tarifa de la BD en los parámetros que usa el cálculo. */
    @Transactional(readOnly = true)
    public TarifaConfig configurar(Tarifa tarifa) {

        List<TarifaConfig.Tramo> tramos = tramoRepository
                .findByTarifaIdOrderByDesdeKwh(tarifa.getId()).stream()
                .map(t -> new TarifaConfig.Tramo(
                        t.getDesdeKwh(), t.getHastaKwh(), t.getPrecioKwh()))
                .toList();

        List<TarifaConfig.Franja> franjas = franjaRepository
                .findByTarifaIdOrderByHoraInicio(tarifa.getId()).stream()
                .map(f -> new TarifaConfig.Franja(
                        f.getNombre(), f.getHoraInicio(), f.getHoraFin(),
                        f.getPrecioKwh()))
                .toList();

        return new TarifaConfig(tarifa.getModalidad(), tarifa.getNombre(),
                tarifa.getPrecioKwh(), tramos, franjas);
    }

    private TarifaResponse responder(Tarifa t) {
        return TarifaResponse.desde(t,
                tramoRepository.findByTarifaIdOrderByDesdeKwh(t.getId()),
                franjaRepository.findByTarifaIdOrderByHoraInicio(t.getId()));
    }

    /**
     * Mantiene una sola tarifa vigente por tipo de cliente: la nueva debe
     * empezar después de la actual, y esta se cierra el día anterior.
     */
    private void cerrarTarifaAnterior(TipoCliente tipo, LocalDate nuevaDesde) {
        List<Tarifa> existentes = tarifaRepository
                .findByTipoClienteAndActivaTrueOrderByVigenteDesdeDesc(tipo);

        if (existentes.isEmpty()) {
            return;
        }

        Tarifa ultima = existentes.get(0);

        if (!nuevaDesde.isAfter(ultima.getVigenteDesde())) {
            throw error(HttpStatus.CONFLICT,
                    "La nueva tarifa debe iniciar después del "
                            + ultima.getVigenteDesde()
                            + " (inicio de la tarifa vigente)");
        }

        LocalDate fin = ultima.getVigenteHasta();
        if (fin == null || !fin.isBefore(nuevaDesde)) {
            ultima.setVigenteHasta(nuevaDesde.minusDays(1));
        }
    }

    private void validarPlana(
            BigDecimal precio,
            List<TramoRequest> tramos,
            List<FranjaRequest> franjas) {

        if (precio == null || precio.signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa PLANA requiere precioKwh mayor que cero");
        }
        if (!tramos.isEmpty() || !franjas.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa PLANA no usa tramos ni franjas");
        }
    }

    private void validarEscalonada(
            List<TramoRequest> tramos, List<FranjaRequest> franjas) {

        if (tramos.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa ESCALONADA requiere al menos un tramo");
        }
        if (!franjas.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa ESCALONADA no usa franjas");
        }

        for (TramoRequest t : tramos) {
            if (t.desdeKwh() == null || t.precioKwh() == null
                    || t.precioKwh().signum() < 0) {
                throw error(HttpStatus.BAD_REQUEST,
                        "Cada tramo requiere desdeKwh y un precioKwh >= 0");
            }
        }

        List<TramoRequest> ordenados = ordenarTramos(tramos);
        BigDecimal esperado = BigDecimal.ZERO;

        for (int i = 0; i < ordenados.size(); i++) {
            TramoRequest tramo = ordenados.get(i);
            boolean ultimo = i == ordenados.size() - 1;

            if (tramo.desdeKwh().compareTo(esperado) != 0) {
                throw error(HttpStatus.BAD_REQUEST,
                        "Los tramos deben ser continuos y el primero debe "
                                + "empezar en 0 kWh");
            }

            if (ultimo) {
                if (tramo.hastaKwh() != null) {
                    throw error(HttpStatus.BAD_REQUEST,
                            "El último tramo debe ser abierto (sin hastaKwh)");
                }
            } else {
                if (tramo.hastaKwh() == null
                        || tramo.hastaKwh().compareTo(tramo.desdeKwh()) <= 0) {
                    throw error(HttpStatus.BAD_REQUEST,
                            "Cada tramo (menos el último) requiere un hastaKwh "
                                    + "mayor que su desdeKwh");
                }
                esperado = tramo.hastaKwh();
            }
        }
    }

    private void validarHoraria(
            List<FranjaRequest> franjas, List<TramoRequest> tramos) {

        if (franjas.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa HORARIA requiere franjas");
        }
        if (!tramos.isEmpty()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La tarifa HORARIA no usa tramos");
        }

        List<TarifaConfig.Franja> validas = new ArrayList<>();

        for (FranjaRequest f : franjas) {
            if (esVacio(f.nombre()) || f.horaInicio() == null
                    || f.horaFin() == null || f.precioKwh() == null
                    || f.precioKwh().signum() < 0) {
                throw error(HttpStatus.BAD_REQUEST,
                        "Cada franja requiere nombre, horaInicio, horaFin "
                                + "y un precioKwh >= 0");
            }
            if (f.horaInicio() < 0 || f.horaInicio() > 23
                    || f.horaFin() < 0 || f.horaFin() > 24
                    || f.horaInicio().equals(f.horaFin())) {
                throw error(HttpStatus.BAD_REQUEST,
                        "horaInicio va de 0 a 23, horaFin de 0 a 24 y no "
                                + "pueden ser iguales");
            }
            validas.add(new TarifaConfig.Franja(
                    f.nombre(), f.horaInicio(), f.horaFin(), f.precioKwh()));
        }

        // Cada hora del día debe pertenecer a una y solo una franja
        for (int hora = 0; hora < 24; hora++) {
            int contador = 0;
            for (TarifaConfig.Franja franja : validas) {
                if (franja.cubre(hora)) {
                    contador++;
                }
            }
            if (contador != 1) {
                throw error(HttpStatus.BAD_REQUEST,
                        "Las franjas deben cubrir las 24 horas sin "
                                + "superponerse (problema en la hora " + hora
                                + ")");
            }
        }
    }

    private List<TramoRequest> ordenarTramos(List<TramoRequest> tramos) {
        return tramos.stream()
                .sorted(Comparator.comparing(TramoRequest::desdeKwh))
                .toList();
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}