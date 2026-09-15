package com.solumeca.controller;

import com.solumeca.model.MensajeContacto;
import com.solumeca.repository.MensajeContactoRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class ContactoController {

    private static final Logger log = LoggerFactory.getLogger(ContactoController.class);

    private final JavaMailSender mailSender;
    private final MensajeContactoRepository mensajeContactoRepository;
    private final String fromEmail;
    private final String destinationEmail;

    public ContactoController(JavaMailSender mailSender,
                              MensajeContactoRepository mensajeContactoRepository,
                              @Value("${spring.mail.username:solumeca.2@gmail.com}") String fromEmail,
                              @Value("${app.mail.to:${spring.mail.username:solumeca.2@gmail.com}}") String destinationEmail) {
        this.mailSender = mailSender;
        this.mensajeContactoRepository = mensajeContactoRepository;
        this.fromEmail = fromEmail;
        this.destinationEmail = destinationEmail;
    }

    @PostMapping("/api/contacto")
    public ResponseEntity<Map<String, String>> enviar(@Valid @RequestBody ContactRequest request) {
        log.info("Solicitud de contacto recibida de: {} <{}>", request.name(), request.email());

        // 1. Guardar mensaje en base de datos para trazabilidad y asegurar que nunca se pierda un prospecto
        MensajeContacto mensajeGuardado = new MensajeContacto(
                request.name(),
                request.email(),
                request.phone(),
                request.message(),
                LocalDateTime.now(),
                false
        );
        try {
            mensajeGuardado = mensajeContactoRepository.save(mensajeGuardado);
        } catch (Exception e) {
            log.warn("No se pudo persistir el mensaje de contacto en BD: {}", e.getMessage());
        }

        // 2. Enviar correo electronico via JavaMailSender (SMTP)
        boolean correoEnviado = false;
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(fromEmail);
            mail.setTo(destinationEmail);
            mail.setReplyTo(request.email());
            mail.setSubject("Nuevo contacto desde SOLUMECA: " + request.name());
            mail.setText("Nuevo mensaje de contacto recibido a traves del sitio web de SOLUMECA S.A.S.:\n\n"
                    + "• Nombre: " + request.name() + "\n"
                    + "• Correo: " + request.email() + "\n"
                    + "• Telefono: " + (request.phone().isBlank() ? "No indicado" : request.phone()) + "\n\n"
                    + "Mensaje:\n" + request.message() + "\n\n"
                    + "---\n"
                    + "Enviado automaticamente desde solumeca.up.railway.app");
            mailSender.send(mail);
            correoEnviado = true;
            log.info("Correo de contacto enviado exitosamente a: {}", destinationEmail);

            if (mensajeGuardado.getId() != null) {
                mensajeGuardado.setEnviadoPorCorreo(true);
                mensajeContactoRepository.save(mensajeGuardado);
            }
        } catch (Exception exception) {
            log.error("No se pudo despachar el correo por SMTP hacia {}: {}", destinationEmail, exception.getMessage());
        }

        if (correoEnviado) {
            return ResponseEntity.ok(Map.of("message", "¡Mensaje enviado con éxito al correo! Nos pondremos en contacto pronto."));
        } else {
            return ResponseEntity.ok(Map.of("message", "¡Mensaje recibido correctamente en nuestro sistema! Nos pondremos en contacto pronto."));
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