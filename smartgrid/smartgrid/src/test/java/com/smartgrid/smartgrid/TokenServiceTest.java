package com.smartgrid.smartgrid;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.smartgrid.smartgrid.model.Rol;
import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private final SecretKey clave = new SecretKeySpec(
            "clave-de-prueba-de-al-menos-32-bytes!!"
                    .getBytes(StandardCharsets.UTF_8),
            "HmacSHA256");

    @Test
    void tokenContieneUsuarioRolYVencimiento() {
        TokenService servicio = new TokenService(
                new NimbusJwtEncoder(new ImmutableSecret<>(clave)), 60);

        JwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(clave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        Usuario admin = new Usuario("Admin", "admin", "x", Rol.ADMIN, true);

        Jwt jwt = decoder.decode(servicio.generar(admin));

        assertEquals("admin", jwt.getSubject());
        assertEquals("ADMIN", jwt.getClaimAsString("rol"));
        assertTrue(jwt.getExpiresAt().isAfter(Instant.now()));
    }
}