package com.mercadoyuli.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.mail.username}")
    private String remitente;

    // Si hay API key de Brevo se envia por HTTPS (funciona donde el SMTP
    // esta bloqueado, ej. Render). Si no, se usa SMTP (util en local).
    @Value("${brevo.api-key:}")
    private String brevoApiKey;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarRecuperacion(String destinatario, String enlace) {
        String asunto = "Recuperacion de contrasena - Mercado Yuli";
        String cuerpo = "Hola,\n\n" +
                "Recibimos una solicitud para restablecer tu contrasena.\n" +
                "Haz clic en el siguiente enlace (valido por 30 minutos):\n\n" +
                enlace + "\n\n" +
                "Si no solicitaste este cambio, ignora este correo.\n\n" +
                "Mercado Yuli Online";

        // 1) Envio por API de Brevo (HTTPS) si esta configurada
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            if (enviarPorBrevo(destinatario, asunto, cuerpo)) return;
        }

        // 2) Respaldo: envio por SMTP (funciona en local)
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(destinatario);
            msg.setSubject(asunto);
            msg.setText(cuerpo);
            if (remitente != null && !remitente.isBlank() && !remitente.startsWith("TU_")) {
                msg.setFrom(remitente);
            }
            mailSender.send(msg);
            log.info("Correo de recuperacion enviado (SMTP) a {}", destinatario);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo (SMTP ni Brevo). " +
                    "Enlace de recuperacion para {}: {}", destinatario, enlace);
        }
    }

    private boolean enviarPorBrevo(String destinatario, String asunto, String cuerpo) {
        try {
            String from = (remitente != null && !remitente.isBlank() && !remitente.startsWith("TU_"))
                    ? remitente : "no-reply@mercadoyuli.com";
            String json = objectMapper.writeValueAsString(Map.of(
                    "sender", Map.of("name", "Mercado Yuli", "email", from),
                    "to", List.of(Map.of("email", destinatario)),
                    "subject", asunto,
                    "textContent", cuerpo
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_URL))
                    .header("api-key", brevoApiKey)
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> resp = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                log.info("Correo de recuperacion enviado (Brevo) a {}", destinatario);
                return true;
            }
            log.warn("Brevo respondio {}: {}", resp.statusCode(), resp.body());
            return false;
        } catch (Exception e) {
            log.warn("Error enviando por Brevo: {}", e.getMessage());
            return false;
        }
    }
}
