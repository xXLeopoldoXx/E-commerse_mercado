package com.mercadoyuli.service;

import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.repository.UsuarioRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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

    // Usuario autenticado actualmente (desde el SecurityContext de Spring Security)
    public Optional<Usuario> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }
        return usuarioRepository.findByEmail(auth.getName());
    }

    // ===================== Recuperacion de contrasena =====================

    /**
     * Genera un token de recuperacion (valido 30 min) para el correo dado.
     * Devuelve el token si el correo existe; vacio en caso contrario.
     */
    public Optional<String> generarTokenRecuperacion(String email) {
        return usuarioRepository.findByEmail(email).map(u -> {
            String token = UUID.randomUUID().toString();
            u.setResetToken(token);
            u.setResetTokenExpira(LocalDateTime.now().plusMinutes(30));
            usuarioRepository.save(u);
            return token;
        });
    }

    /** Busca un usuario por token de recuperacion vigente (no expirado). */
    public Optional<Usuario> buscarPorTokenValido(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        return usuarioRepository.findByResetToken(token)
                .filter(u -> u.getResetTokenExpira() != null
                        && u.getResetTokenExpira().isAfter(LocalDateTime.now()));
    }

    /** Restablece la contrasena a partir de un token valido. Devuelve true si tuvo exito. */
    public boolean restablecerPassword(String token, String nuevaPassword) {
        return buscarPorTokenValido(token).map(u -> {
            u.setPassword(passwordEncoder.encode(nuevaPassword));
            u.setResetToken(null);
            u.setResetTokenExpira(null);
            usuarioRepository.save(u);
            return true;
        }).orElse(false);
    }
}

