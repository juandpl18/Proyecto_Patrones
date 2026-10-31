package com.smartgrid.smartgrid.adapter;

import com.smartgrid.smartgrid.model.Usuario;
import com.smartgrid.smartgrid.repository.UsuarioRepository;
import com.smartgrid.smartgrid.service.Autenticacion;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacionLocalAdapter implements Autenticacion {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AutenticacionLocalAdapter(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario autenticar(String username, String password) {

        return usuarioRepository.findByUsername(username)
                .filter(Usuario::isActivo)
                .filter(usuario ->
                        passwordEncoder.matches(
                                password,
                                usuario.getPassword()
                        )
                )
                .orElse(null);
    }
}