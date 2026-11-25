package com.smartgrid.smartgrid.service;

import com.smartgrid.smartgrid.dto.CargoResponse;
import com.smartgrid.smartgrid.dto.CrearCargoRequest;
import com.smartgrid.smartgrid.model.CargoAdicional;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.TipoCalculoCargo;
import com.smartgrid.smartgrid.model.TipoCliente;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.CargoAdicionalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CargoService {

    private static final String PATRON_CODIGO = "^[A-Z0-9_]{3,40}$";

    private final CargoAdicionalRepository cargoRepository;
    private final AutorizacionService autorizacionService;

    public CargoService(
            CargoAdicionalRepository cargoRepository,
            AutorizacionService autorizacionService) {

        this.cargoRepository = cargoRepository;
        this.autorizacionService = autorizacionService;
    }

    @Transactional
    public CargoResponse crear(Usuario solicitante, CrearCargoRequest datos) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        if (datos == null || esVacio(datos.codigo()) || esVacio(datos.nombre())
                || esVacio(datos.tipoCalculo()) || datos.valor() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "codigo, nombre, tipoCalculo y valor son obligatorios");
        }

        String codigo = datos.codigo().trim().toUpperCase();
        if (!codigo.matches(PATRON_CODIGO)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El código debe tener de 3 a 40 caracteres: "
                            + "letras, números y guion bajo");
        }

        TipoCalculoCargo tipoCalculo = EnumParser.parsear(
                TipoCalculoCargo.class, datos.tipoCalculo(), "tipoCalculo");

        TipoCliente tipoCliente = esVacio(datos.tipoCliente())
                ? null
                : EnumParser.parsear(
                TipoCliente.class, datos.tipoCliente(), "tipoCliente");

        if (tipoCalculo == TipoCalculoCargo.PORCENTAJE
                && (datos.valor().compareTo(new BigDecimal("-100")) < 0
                || datos.valor().compareTo(new BigDecimal("100")) > 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Un porcentaje debe estar entre -100 y 100");
        }

        if (cargoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cargo con el código: " + codigo);
        }

        CargoAdicional cargo = new CargoAdicional();
        cargo.setCodigo(codigo);
        cargo.setNombre(datos.nombre().trim());
        cargo.setTipoCliente(tipoCliente);
        cargo.setTipoCalculo(tipoCalculo);
        cargo.setValor(datos.valor().setScale(4, RoundingMode.HALF_UP));
        cargo.setActivo(true);

        return CargoResponse.desde(cargoRepository.save(cargo));
    }

    @Transactional(readOnly = true)
    public List<CargoResponse> listar() {
        return cargoRepository.findAllByOrderById().stream()
                .map(CargoResponse::desde)
                .toList();
    }

    @Transactional
    public CargoResponse cambiarEstado(
            Usuario solicitante, Long id, boolean activo) {

        autorizacionService.validarRol(solicitante, Rol.ADMIN);

        CargoAdicional cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Cargo no encontrado: " + id));

        cargo.setActivo(activo);
        return CargoResponse.desde(cargoRepository.save(cargo));
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}