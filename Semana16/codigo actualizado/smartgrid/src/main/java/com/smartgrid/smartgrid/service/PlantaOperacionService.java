package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CambioEstadoResponse;
import com.smartgrid.smartgrid.dto.PlantaDetalleResponse;
import com.smartgrid.smartgrid.model.Alerta;
import com.smartgrid.smartgrid.model.CambioEstadoPlanta;
import com.smartgrid.smartgrid.model.EstadoPlanta;
import com.smartgrid.smartgrid.model.PlantaGeneracion;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Severidad;
import com.smartgrid.smartgrid.model.TipoAlerta;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.AlertaRepository;
import com.smartgrid.smartgrid.repository.CambioEstadoPlantaRepository;
import com.smartgrid.smartgrid.repository.PlantaGeneracionRepository;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import com.smartgrid.smartgrid.state.EstadoPlantaResolver;
import com.smartgrid.smartgrid.state.EstadoPlantaState;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PlantaOperacionService {

    private static final int MOTIVO_MAXIMO = 200;
    private static final int MENSAJE_ALERTA_MAXIMO = 300;

    private final PlantaGeneracionRepository plantaRepository;
    private final ZonaRedRepository zonaRepository;
    private final CambioEstadoPlantaRepository historialRepository;
    private final AlertaRepository alertaRepository;
    private final EstadoPlantaResolver resolver;
    private final AutorizacionService autorizacionService;

    public PlantaOperacionService(
            PlantaGeneracionRepository plantaRepository,
            ZonaRedRepository zonaRepository,
            CambioEstadoPlantaRepository historialRepository,
            AlertaRepository alertaRepository,
            EstadoPlantaResolver resolver,
            AutorizacionService autorizacionService) {

        this.plantaRepository = plantaRepository;
        this.zonaRepository = zonaRepository;
        this.historialRepository = historialRepository;
        this.alertaRepository = alertaRepository;
        this.resolver = resolver;
        this.autorizacionService = autorizacionService;
    }

    @Transactional(readOnly = true)
    public PlantaDetalleResponse obtener(Long id) {
        return detalle(buscar(id));
    }

    @Transactional
    public PlantaDetalleResponse cambiarEstado(
            Usuario solicitante,
            Long id,
            String estadoDestino,
            String motivo) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        EstadoPlanta destino = parsearEstado(estadoDestino);

        if (motivo != null && motivo.length() > MOTIVO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El motivo no puede superar " + MOTIVO_MAXIMO
                            + " caracteres");
        }

        PlantaGeneracion planta = buscar(id);
        EstadoPlanta anterior = planta.getEstado();
        EstadoPlantaState actual = resolver.de(anterior);

        if (anterior == destino) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La planta ya está en estado " + destino);
        }

        if (!actual.puedePasarA(destino)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transición no permitida: " + anterior + " -> " + destino
                            + ". Permitidas desde " + anterior + ": "
                            + nombres(actual));
        }

        boolean motivoVacio = motivo == null || motivo.isBlank();
        if (destino == EstadoPlanta.FUERA_DE_SERVICIO && motivoVacio) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El motivo es obligatorio para sacar una planta de servicio");
        }

        planta.setEstado(destino);
        plantaRepository.save(planta);

        CambioEstadoPlanta cambio = new CambioEstadoPlanta();
        cambio.setPlanta(planta);
        cambio.setEstadoAnterior(anterior);
        cambio.setEstadoNuevo(destino);
        cambio.setMotivo(motivoVacio ? null : motivo.trim());
        cambio.setUsuario(solicitante.getUsername());
        historialRepository.save(cambio);

        if (destino == EstadoPlanta.FUERA_DE_SERVICIO) {
            registrarAlertaDeFalla(planta, motivo.trim());
        }

        return detalle(planta);
    }

    @Transactional(readOnly = true)
    public List<CambioEstadoResponse> historial(Long id) {
        buscar(id);
        return historialRepository.findByPlantaIdOrderByFechaHoraDesc(id)
                .stream()
                .map(CambioEstadoResponse::desde)
                .toList();
    }

    @Transactional
    public PlantaDetalleResponse asignarZona(
            Usuario solicitante, Long id, Long zonaId) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        PlantaGeneracion planta = buscar(id);

        ZonaRed zona = zonaRepository.findById(zonaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Zona no encontrada: " + zonaId));

        planta.setZona(zona);
        plantaRepository.save(planta);

        return detalle(planta);
    }

    private void registrarAlertaDeFalla(PlantaGeneracion planta, String motivo) {
        String mensaje = "Planta '" + planta.getNombre()
                + "' fuera de servicio: " + motivo;

        Alerta alerta = new Alerta();
        alerta.setZona(planta.getZona());
        alerta.setTipo(TipoAlerta.FALLA_PLANTA);
        alerta.setSeveridad(Severidad.CRITICA);
        alerta.setMensaje(mensaje.length() > MENSAJE_ALERTA_MAXIMO
                ? mensaje.substring(0, MENSAJE_ALERTA_MAXIMO) : mensaje);

        alertaRepository.save(alerta);
    }

    private PlantaGeneracion buscar(Long id) {
        return plantaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Planta no encontrada: " + id));
    }

    private EstadoPlanta parsearEstado(String texto) {
        try {
            return EstadoPlanta.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Opciones: ACTIVA, MANTENIMIENTO "
                            + "o FUERA_DE_SERVICIO");
        }
    }

    private List<String> nombres(EstadoPlantaState estado) {
        return estado.transicionesPermitidas().stream()
                .map(Enum::name)
                .sorted()
                .toList();
    }

    private PlantaDetalleResponse detalle(PlantaGeneracion p) {
        ZonaRed zona = p.getZona();

        return new PlantaDetalleResponse(
                p.getId(),
                p.getNombre(),
                p.getTipoFuente().name(),
                p.getCapacidadKw(),
                p.getVoltajeSalida(),
                p.isTieneBateriaReserva(),
                p.getUbicacion(),
                p.getEstado().name(),
                nombres(resolver.de(p.getEstado())),
                zona != null ? zona.getId() : null,
                zona != null ? zona.getNombre() : null);
    }
}