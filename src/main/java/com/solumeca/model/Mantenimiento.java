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

    @Column(name = "analisis", columnDefinition = "TEXT")
    private String analisis;

    @Column(name = "solucion", columnDefinition = "TEXT")
    private String solucion;

    @Column(name = "costo_estimado")
    private Double costoEstimado;

    @Column(name = "dias_estimados")
    private Integer diasEstimados;

    @Column(name = "valor_total")
    private Double valorTotal;

    @Column(name = "numero_orden", length = 50)
    private String numeroOrden;

    @Column(name = "fecha_aprobacion")
    private LocalDate fechaAprobacion;

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMaquinariaId() { return maquinariaId; }
    public void setMaquinariaId(Long maquinariaId) { this.maquinariaId = maquinariaId; }

    public String getSolicitante() {
        if (solicitante != null && solicitante.length() > 30) {
            return solicitante.substring(0, 30);
        }
        return solicitante;
    }
    public void setSolicitante(String solicitante) {
        if (solicitante != null) {
            String trimmed = solicitante.trim();
            this.solicitante = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.solicitante = null;
        }
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() {
        if (descripcion != null && descripcion.length() > 250) {
            return descripcion.substring(0, 250);
        }
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        if (descripcion != null) {
            String trimmed = descripcion.trim();
            this.descripcion = trimmed.length() > 250 ? trimmed.substring(0, 250) : trimmed;
        } else {
            this.descripcion = null;
        }
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTecnicoAsignado() {
        if (tecnicoAsignado != null && tecnicoAsignado.length() > 30) {
            return tecnicoAsignado.substring(0, 30);
        }
        return tecnicoAsignado;
    }
    public void setTecnicoAsignado(String tecnicoAsignado) {
        if (tecnicoAsignado != null) {
            String trimmed = tecnicoAsignado.trim();
            this.tecnicoAsignado = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.tecnicoAsignado = null;
        }
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getArchivosEvidencia() { return archivosEvidencia; }
    public void setArchivosEvidencia(String archivosEvidencia) { this.archivosEvidencia = archivosEvidencia; }
    public String getInformeArchivo() { return informeArchivo; }
    public void setInformeArchivo(String informeArchivo) { this.informeArchivo = informeArchivo; }

    public String getAnalisis() {
        if (analisis != null && analisis.length() > 50) {
            return analisis.substring(0, 50);
        }
        return analisis;
    }
    public void setAnalisis(String analisis) {
        if (analisis != null) {
            String trimmed = analisis.trim();
            this.analisis = trimmed.length() > 50 ? trimmed.substring(0, 50) : trimmed;
        } else {
            this.analisis = null;
        }
    }

    public String getSolucion() {
        if (solucion != null && solucion.length() > 200) {
            return solucion.substring(0, 200);
        }
        return solucion;
    }
    public void setSolucion(String solucion) {
        if (solucion != null) {
            String trimmed = solucion.trim();
            this.solucion = trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
        } else {
            this.solucion = null;
        }
    }

    public Double getCostoEstimado() { return costoEstimado; }
    public void setCostoEstimado(Double costoEstimado) { this.costoEstimado = costoEstimado; }
    public Integer getDiasEstimados() { return diasEstimados; }
    public void setDiasEstimados(Integer diasEstimados) { this.diasEstimados = diasEstimados; }
    public Double getValorTotal() { return valorTotal; }
    public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }
    public String getNumeroOrden() { return numeroOrden; }
    public void setNumeroOrden(String numeroOrden) { this.numeroOrden = numeroOrden; }
    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public String getMotivoRechazo() {
        if (motivoRechazo != null && motivoRechazo.length() > 200) {
            return motivoRechazo.substring(0, 200);
        }
        return motivoRechazo;
    }
    public void setMotivoRechazo(String motivoRechazo) {
        if (motivoRechazo != null) {
            String trimmed = motivoRechazo.trim();
            this.motivoRechazo = trimmed.length() > 200 ? trimmed.substring(0, 200) : trimmed;
        } else {
            this.motivoRechazo = null;
        }
    }
}
