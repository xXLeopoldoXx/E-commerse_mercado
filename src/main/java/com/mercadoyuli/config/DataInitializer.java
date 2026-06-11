package com.mercadoyuli.config;

import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void seedAdmin() {
        if (usuarioRepository.existsByEmail("admin@mercadoyuli.com")) return;

        Usuario admin = new Usuario();
        admin.setNombre("Administrador Yuli");
        admin.setDni("00000000");
        admin.setEmail("admin@mercadoyuli.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRol("ADMIN");
        usuarioRepository.save(admin);
    }
}
