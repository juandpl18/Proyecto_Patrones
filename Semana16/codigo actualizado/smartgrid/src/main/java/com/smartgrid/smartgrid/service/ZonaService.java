package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CrearZonaRequest;
import com.smartgrid.smartgrid.dto.ZonaResponse;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.TipoZona;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.model.ZonaRed;
import com.smartgrid.smartgrid.repository.ZonaRedRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ZonaService {

    private final ZonaRedRepository zonaRepository;
    private final AutorizacionService autorizacionService;

    public ZonaService(
            ZonaRedRepository zonaRepository,
            AutorizacionService autorizacionService) {

        this.zonaRepository = zonaRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional
    public ZonaResponse crear(Usuario solicitante, CrearZonaRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (datos == null || datos.nombre() == null || datos.nombre().isBlank()
                || datos.tipo() == null || datos.tipo().isBlank()) {
            throw error(HttpStatus.BAD_REQUEST,
                    "nombre y tipo son obligatorios");
        }

        if (datos.capacidadKw() != null && datos.capacidadKw().signum() <= 0) {
            throw error(HttpStatus.BAD_REQUEST,
                    "La capacidad debe ser mayor que cero");
        }

        TipoZona tipo = parsearTipo(datos.tipo());

        ZonaRed padre = null;
        if (datos.zonaPadreId() != null) {
            padre = zonaRepository.findById(datos.zonaPadreId())
                    .orElseThrow(() -> error(HttpStatus.NOT_FOUND,
                            "Zona padre no encontrada: " + datos.zonaPadreId()));
        }

        validarJerarquia(tipo, padre);

        ZonaRed zona = new ZonaRed();
        zona.setNombre(datos.nombre().trim());
        zona.setTipo(tipo);
        zona.setZonaPadre(padre);
        zona.setCapacidadKw(datos.capacidadKw());

        return ZonaResponse.desde(zonaRepository.save(zona));
    }

    @Transactional(readOnly = true)
    public List<ZonaResponse> listar() {
        return zonaRepository.findAll().stream()
                .map(ZonaResponse::desde)
                .toList();
    }

    /** SUBESTACION (raíz) -> ZONA -> SECTOR. */
    private void validarJerarquia(TipoZona tipo, ZonaRed padre) {
        switch (tipo) {
            case SUBESTACION -> {
                if (padre != null) {
                    throw error(HttpStatus.BAD_REQUEST,
                            "Una subestación no puede tener zona padre");
                }
            }
            case ZONA -> {
                if (padre == null || padre.getTipo() != TipoZona.SUBESTACION) {
                    throw error(HttpStatus.BAD_REQUEST,
                            "Una zona debe pertenecer a una subestación");
                }
            }
            case SECTOR -> {
                if (padre == null || padre.getTipo() != TipoZona.ZONA) {
                    throw error(HttpStatus.BAD_REQUEST,
                            "Un sector debe pertenecer a una zona");
                }
            }
        }
    }

    private TipoZona parsearTipo(String texto) {
        try {
            return TipoZona.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw error(HttpStatus.BAD_REQUEST,
                    "Tipo inválido. Opciones: SUBESTACION, ZONA o SECTOR");
        }
    }

    private ResponseStatusException error(HttpStatus estado, String mensaje) {
        return new ResponseStatusException(estado, mensaje);
    }
}