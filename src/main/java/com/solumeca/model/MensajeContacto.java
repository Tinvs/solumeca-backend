package com.solumeca.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes_contacto")
public class MensajeContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String correo;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 2000)
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "enviado_por_correo")
    private Boolean enviadoPorCorreo = false;

    public MensajeContacto() {
    }

    public MensajeContacto(String nombre, String correo, String telefono, String mensaje, LocalDateTime fechaEnvio, Boolean enviadoPorCorreo) {
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
        this.enviadoPorCorreo = enviadoPorCorreo != null ? enviadoPorCorreo : false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public Boolean getEnviadoPorCorreo() {
        return enviadoPorCorreo;
    }

    public void setEnviadoPorCorreo(Boolean enviadoPorCorreo) {
        this.enviadoPorCorreo = enviadoPorCorreo;
    }
}

