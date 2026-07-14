package com.mercadoyuli.config;

import com.mercadoyuli.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;

/**
 * Configuracion de Spring Security con autenticacion y autorizacion
 * basada en JWT (sin estado). Las contrasenas se cifran con BCrypt.
 *
 *  - /admin/**   requiere rol ADMIN
 *  - /mi-cuenta  requiere estar autenticado
 *  - el resto es publico
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // La autenticacion es por JWT (sin estado), pero el carrito usa la
            // sesion HTTP (@SessionScope). Por eso permitimos crear sesion cuando
            // se necesite (para el carrito)...
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            // ...pero la AUTENTICACION NO se guarda en la sesion: se deriva del JWT
            // en cada peticion. Asi el logout y la expiracion del token si cierran
            // la sesion (de lo contrario la sesion mantendria vivo el login).
            .securityContext(sc -> sc.securityContextRepository(
                    new RequestAttributeSecurityContextRepository()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/admin/login").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Para comprar hay que estar autenticado (checkout y confirmacion del pedido)
                .requestMatchers("/checkout/**", "/mi-cuenta").authenticated()
                .anyRequest().permitAll()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, e) -> {
                    String uri = request.getRequestURI();
                    if (uri.startsWith("/admin")) {
                        response.sendRedirect("/admin/login");
                    } else if (uri.startsWith("/checkout")) {
                        // Debe iniciar sesion para comprar: volver al carrito con aviso
                        response.sendRedirect("/carrito?login=1");
                    } else {
                        response.sendRedirect("/");
                    }
                })
                .accessDeniedHandler((request, response, e) ->
                        response.sendRedirect(request.getRequestURI().startsWith("/admin")
                                ? "/admin/login" : "/"))
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
