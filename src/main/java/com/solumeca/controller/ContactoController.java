package com.solumeca.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContactoController {

    private final JavaMailSender mailSender;
    private final String destinationEmail;

    public ContactoController(JavaMailSender mailSender,
                              @Value("${spring.mail.username}") String destinationEmail) {
        this.mailSender = mailSender;
        this.destinationEmail = destinationEmail;
    }

    @PostMapping("/api/contacto")
    public ResponseEntity<Map<String, String>> enviar(@Valid @RequestBody ContactRequest request) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(destinationEmail);
            mail.setReplyTo(request.email());
            mail.setSubject("Nuevo contacto desde SOLUMECA: " + request.name());
            mail.setText("Nombre: " + request.name()
                    + "\nCorreo: " + request.email()
                    + "\nTeléfono: " + (request.phone().isBlank() ? "No indicado" : request.phone())
                    + "\n\nMensaje:\n" + request.message());
            mailSender.send(mail);
            return ResponseEntity.ok(Map.of("message", "¡Mensaje enviado! Nos pondremos en contacto pronto."));
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "No se pudo enviar el mensaje. Verifica la configuración del correo."));
        }
    }

    public record ContactRequest(
            @NotBlank @Size(max = 100) String name,
            @NotBlank @Email @Size(max = 150) String email,
            @Size(max = 30) String phone,
            @NotBlank @Size(min = 10, max = 2000) String message) {

        public ContactRequest {
            phone = phone == null ? "" : phone;
        }
    }
}