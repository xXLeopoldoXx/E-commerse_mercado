package com.mercadoyuli.security;

import com.mercadoyuli.model.Usuario;
import com.mercadoyuli.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Carga el usuario desde la BD para la autenticacion de Spring Security.
 * El rol del usuario ("ADMIN" / "USUARIO") se mapea al authority "ROLE_...".
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        String rol = u.getRol() != null ? u.getRol() : "USUARIO";
        return new User(u.getEmail(), u.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
    }
}
