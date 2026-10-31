package com.smartgrid.smartgrid.controller;

import com.smartgrid.smartgrid.dto.LoginRequest;
import com.smartgrid.smartgrid.dto.LoginResponse;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.AutenticacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AutenticacionService autenticacionService;

    public AuthController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        Usuario usuario = autenticacionService.autenticar(
                request.getUsername(),
                request.getPassword()
        );

        if (usuario == null) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse(
                            "Usuario o contraseña incorrectos",
                            null,
                            null
                    ));
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        "Inicio de sesión exitoso",
                        usuario.getUsername(),
                        usuario.getRol()
                )
        );
    }
}