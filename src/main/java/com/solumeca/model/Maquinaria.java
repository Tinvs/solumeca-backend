package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "maquinaria")
public class Maquinaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    private String modelo;

    @Column(name = "numero_serie", unique = true, nullable = false)
    private String numeroSerie;

    @Column(nullable = false)
    private String estado; // Operativa, En mantenimiento, Fuera de servicio

    public Maquinaria() {}

    public Maquinaria(String codigo, String nombre, String marca, String modelo, String numeroSerie, String estado) {
        setCodigo(codigo);
        setNombre(nombre);
        setMarca(marca);
        setModelo(modelo);
        setNumeroSerie(numeroSerie);
        this.estado = estado;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() {
        if (codigo != null && codigo.length() > 20) {
            return codigo.substring(0, 20);
        }
        return codigo;
    }
    public void setCodigo(String codigo) {
        if (codigo != null) {
            String trimmed = codigo.trim();
            this.codigo = trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
        } else {
            this.codigo = null;
        }
    }

    public String getNombre() {
        if (nombre != null && nombre.length() > 20) {
            return nombre.substring(0, 20);
        }
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre != null) {
            String trimmed = nombre.trim();
            this.nombre = trimmed.length() > 20 ? trimmed.substring(0, 20) : trimmed;
        } else {
            this.nombre = null;
        }
    }

    public String getMarca() {
        if (marca != null && marca.length() > 25) {
            return marca.substring(0, 25);
        }
        return marca;
    }
    public void setMarca(String marca) {
        if (marca != null) {
            String trimmed = marca.trim();
            this.marca = trimmed.length() > 25 ? trimmed.substring(0, 25) : trimmed;
        } else {
            this.marca = null;
        }
    }

    public String getModelo() {
        if (modelo != null && modelo.length() > 25) {
            return modelo.substring(0, 25);
        }
        return modelo;
    }
    public void setModelo(String modelo) {
        if (modelo != null) {
            String trimmed = modelo.trim();
            this.modelo = trimmed.length() > 25 ? trimmed.substring(0, 25) : trimmed;
        } else {
            this.modelo = null;
        }
    }

    public String getNumeroSerie() {
        if (numeroSerie != null && numeroSerie.length() > 25) {
            return numeroSerie.substring(0, 25);
        }
        return numeroSerie;
    }
    public void setNumeroSerie(String numeroSerie) {
        if (numeroSerie != null) {
            String trimmed = numeroSerie.trim();
            this.numeroSerie = trimmed.length() > 25 ? trimmed.substring(0, 25) : trimmed;
        } else {
            this.numeroSerie = null;
        }
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
