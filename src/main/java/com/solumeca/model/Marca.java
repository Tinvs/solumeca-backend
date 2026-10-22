package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "marcas")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "pais_origen", length = 100)
    private String paisOrigen;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    public Marca() {}

    public Marca(String nombre, String paisOrigen, String descripcion, Proveedor proveedor) {
        setNombre(nombre);
        setPaisOrigen(paisOrigen);
        setDescripcion(descripcion);
        this.proveedor = proveedor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() {
        if (nombre != null && nombre.length() > 25) {
            return nombre.substring(0, 25);
        }
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre != null) {
            String trimmed = nombre.trim();
            this.nombre = trimmed.length() > 25 ? trimmed.substring(0, 25) : trimmed;
        } else {
            this.nombre = null;
        }
    }

    public String getPaisOrigen() {
        if (paisOrigen != null && paisOrigen.length() > 30) {
            return paisOrigen.substring(0, 30);
        }
        return paisOrigen;
    }
    public void setPaisOrigen(String paisOrigen) {
        if (paisOrigen != null) {
            String trimmed = paisOrigen.trim();
            this.paisOrigen = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.paisOrigen = null;
        }
    }

    public String getDescripcion() {
        if (descripcion != null && descripcion.length() > 150) {
            return descripcion.substring(0, 150);
        }
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        if (descripcion != null) {
            String trimmed = descripcion.trim();
            this.descripcion = trimmed.length() > 150 ? trimmed.substring(0, 150) : trimmed;
        } else {
            this.descripcion = null;
        }
    }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}
