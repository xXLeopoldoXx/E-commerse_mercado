package com.mercadoyuli.service;

import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registrar nuevo usuario (contrasena cifrada con BCrypt)
    public String registrar(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            return "EMAIL_EXISTE";
        }
        if (usuarioRepository.existsByDni(usuario.getDni())) {
            return "DNI_EXISTE";
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioRepository.save(usuario);
        return "OK";
    }

    // Login por email y password (verifica el hash BCrypt)
    public Optional<Usuario> login(String email, String password) {
        return usuarioRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()));
    }

    // Login exclusivo de administradores
    public Optional<Usuario> loginAdmin(String email, String password) {
        return login(email, password)
                .filter(u -> "ADMIN".equals(u.getRol()));
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public long contarUsuarios() {
        return usuarioRepository.count();
    }
}
