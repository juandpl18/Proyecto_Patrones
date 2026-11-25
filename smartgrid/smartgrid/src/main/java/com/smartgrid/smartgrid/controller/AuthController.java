package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.LoginRequest;
import com.smartgrid.smartgrid.dto.LoginResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.AutenticacionService;
import com.smartgrid.smartgrid.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacionService autenticacionService;
    private final TokenService tokenService;

    public AuthController(
            AutenticacionService autenticacionService,
            TokenService tokenService) {

        this.autenticacionService = autenticacionService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        if (request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null
                || request.getPassword().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Usuario y contraseña son obligatorios");
        }

        Usuario usuario = autenticacionService.autenticar(
                request.getUsername(),
                request.getPassword()
        );

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            "Usuario o contraseña incorrectos",
                            null, null, null, 0));
        }

        String token = tokenService.generar(usuario);

        return ResponseEntity.ok(
                new LoginResponse(
                        "Inicio de sesión exitoso",
                        usuario.getUsername(),
                        usuario.getRol(),
                        token,
                        tokenService.getExpiracionSegundos()
                )
        );
    }
}