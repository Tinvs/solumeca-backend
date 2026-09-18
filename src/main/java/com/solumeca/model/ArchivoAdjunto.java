package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "archivos_adjuntos")
public class ArchivoAdjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String nombre;

    @Lob
    @Column(name = "contenido", columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @Column(name = "tipo_contenido", length = 100)
    private String tipoContenido;

    public ArchivoAdjunto() {}

    public ArchivoAdjunto(String nombre, byte[] contenido, String tipoContenido) {
        this.nombre = nombre;
        this.contenido = contenido;
        this.tipoContenido = tipoContenido;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public String getTipoContenido() {
        return tipoContenido;
    }

    public void setTipoContenido(String tipoContenido) {
        this.tipoContenido = tipoContenido;
    }
}
