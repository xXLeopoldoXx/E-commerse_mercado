package com.mercadoyuli.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envia el correo de recuperacion. Si el SMTP no esta configurado o falla,
     * registra el enlace en el log para poder continuar el flujo de prueba.
     */
    public void enviarRecuperacion(String destinatario, String enlace) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(destinatario);
            msg.setSubject("Recuperacion de contrasena - Mercado Yuli");
            msg.setText("Hola,\n\n" +
                    "Recibimos una solicitud para restablecer tu contrasena.\n" +
                    "Haz clic en el siguiente enlace (valido por 30 minutos):\n\n" +
                    enlace + "\n\n" +
                    "Si no solicitaste este cambio, ignora este correo.\n\n" +
                    "Mercado Yuli Online");
            if (remitente != null && !remitente.isBlank() && !remitente.startsWith("TU_")) {
                msg.setFrom(remitente);
            }
            mailSender.send(msg);
            log.info("Correo de recuperacion enviado a {}", destinatario);
        } catch (Exception e) {
            log.warn("No se pudo enviar el correo (revisa la config SMTP). " +
                    "Enlace de recuperacion para {}: {}", destinatario, enlace);
        }
    }
}
