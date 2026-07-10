package com.mercadoyuli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Provee el codificador BCrypt para cifrar/verificar contrasenas.
 * Se usa solo la libreria spring-security-crypto (no toda la auto-config
 * de Spring Security), asi el resto de la app sigue sin filtros de seguridad.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
