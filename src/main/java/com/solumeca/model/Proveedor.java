package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "proveedores")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String nit;

    @Column(length = 100)
    private String contacto;

    @Column(length = 50)
    private String telefono;

    @Column(length = 150)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String categoria;

    public Proveedor() {}

    public Proveedor(String nombre, String nit, String contacto, String telefono, String email, String direccion, String ciudad, String categoria) {
        setNombre(nombre);
        setNit(nit);
        setContacto(contacto);
        setTelefono(telefono);
        this.email = email;
        setDireccion(direccion);
        setCiudad(ciudad);
        setCategoria(categoria);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() {
        if (nombre != null && nombre.length() > 40) {
            return nombre.substring(0, 40);
        }
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre != null) {
            String trimmed = nombre.trim();
            this.nombre = trimmed.length() > 40 ? trimmed.substring(0, 40) : trimmed;
        } else {
            this.nombre = null;
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

    public String getContacto() {
        if (contacto != null && contacto.length() > 35) {
            return contacto.substring(0, 35);
        }
        return contacto;
    }
    public void setContacto(String contacto) {
        if (contacto != null) {
            String trimmed = contacto.trim();
            this.contacto = trimmed.length() > 35 ? trimmed.substring(0, 35) : trimmed;
        } else {
            this.contacto = null;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDireccion() {
        if (direccion != null && direccion.length() > 50) {
            return direccion.substring(0, 50);
        }
        return direccion;
    }
    public void setDireccion(String direccion) {
        if (direccion != null) {
            String trimmed = direccion.trim();
            this.direccion = trimmed.length() > 50 ? trimmed.substring(0, 50) : trimmed;
        } else {
            this.direccion = null;
        }
    }

    public String getCiudad() {
        if (ciudad != null && ciudad.length() > 30) {
            return ciudad.substring(0, 30);
        }
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        if (ciudad != null) {
            String trimmed = ciudad.trim();
            this.ciudad = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.ciudad = null;
        }
    }

    public String getCategoria() {
        if (categoria != null && categoria.length() > 35) {
            return categoria.substring(0, 35);
        }
        return categoria;
    }
    public void setCategoria(String categoria) {
        if (categoria != null) {
            String trimmed = categoria.trim();
            this.categoria = trimmed.length() > 35 ? trimmed.substring(0, 35) : trimmed;
        } else {
            this.categoria = null;
        }
    }
}
