package com.solumeca.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mantenimientos")
public class Mantenimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "maquinaria_id", nullable = false)
    private Long maquinariaId;

    @Column(nullable = false)
    private String solicitante;

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false)
    private String estado;

    @Column(name = "tecnico_asignado")
    private String tecnicoAsignado;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "archivos_evidencia", length = 4000)
    private String archivosEvidencia;

    @Column(name = "informe_archivo", length = 500)
    private String informeArchivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaquinariaId() { return maquinariaId; }
    public void setMaquinariaId(Long maquinariaId) { this.maquinariaId = maquinariaId; }
    public String getSolicitante() { return solicitante; }
    public void setSolicitante(String solicitante) { this.solicitante = solicitante; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTecnicoAsignado() { return tecnicoAsignado; }
    public void setTecnicoAsignado(String tecnicoAsignado) { this.tecnicoAsignado = tecnicoAsignado; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getArchivosEvidencia() { return archivosEvidencia; }
    public void setArchivosEvidencia(String archivosEvidencia) { this.archivosEvidencia = archivosEvidencia; }
    public String getInformeArchivo() { return informeArchivo; }
    public void setInformeArchivo(String informeArchivo) { this.informeArchivo = informeArchivo; }
}
