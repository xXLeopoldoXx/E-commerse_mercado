package com.mercadoyuli.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Genera y valida JSON Web Tokens (JWT) firmados con HMAC-SHA256,
 * y los transporta en una cookie HttpOnly llamada "JWT".
 */
@Service
public class JwtService {

    public static final String COOKIE = "JWT";

    private final SecretKey key;
    private final long expiracionMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expiracionMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiracionMs = expiracionMs;
    }

    public String generar(String email, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(key)
                .compact();
    }

    public String extraerEmail(String token) {
        return parse(token).getSubject();
    }

    public boolean esValido(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
    }

    // ---- Manejo de la cookie ----

    public void agregarCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (expiracionMs / 1000));
        response.addCookie(cookie);
    }

    public void limpiarCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String leerCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if (COOKIE.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
