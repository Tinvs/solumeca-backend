package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol; // "ADMIN", "SUPERVISOR", "TECNICO", "ENCARGADO", "CLIENTE"

    @Column(name = "tipo_cliente")
    private String tipoCliente = "PERSONA"; // "PERSONA" o "EMPRESA"

    @Column(name = "nombre_empresa")
    private String nombreEmpresa;

    @Column(name = "nit")
    private String nit;

    @Column(name = "telefono")
    private String telefono;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() {
        if (username != null && username.length() > 30) {
            return username.substring(0, 30);
        }
        return username;
    }
    public void setUsername(String username) {
        if (username != null) {
            String trimmed = username.trim();
            this.username = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.username = null;
        }
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTipoCliente() { return tipoCliente; }
    public void setTipoCliente(String tipoCliente) { this.tipoCliente = tipoCliente; }

    public String getNombreEmpresa() {
        if (nombreEmpresa != null && nombreEmpresa.length() > 35) {
            return nombreEmpresa.substring(0, 35);
        }
        return nombreEmpresa;
    }
    public void setNombreEmpresa(String nombreEmpresa) {
        if (nombreEmpresa != null) {
            String trimmed = nombreEmpresa.trim();
            this.nombreEmpresa = trimmed.length() > 35 ? trimmed.substring(0, 35) : trimmed;
        } else {
            this.nombreEmpresa = null;
        }
    }

    public String getNit() {
        if (nit != null && nit.length() > 20) {
            return nit.substring(0, 20);
        }
        return nit;
    }
    public void setNit(String nit) {
        if (nit != null) {
            String trimmed = nit.trim();
            this.nit = trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
        } else {
            this.nit = null;
        }
    }

    public String getTelefono() {
        if (telefono != null && telefono.length() > 20) {
            return telefono.substring(0, 20);
        }
        return telefono;
    }
    public void setTelefono(String telefono) {
        if (telefono != null) {
            String trimmed = telefono.trim();
            this.telefono = trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
        } else {
            this.telefono = null;
        }
    }
}
