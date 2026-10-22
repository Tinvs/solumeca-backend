package com.solumeca.model;

import jakarta.persistence.*;

@Entity
@Table(name = "repuestos")
public class Repuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @Column(length = 100)
    private String marca;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "cantidad_stock", nullable = false)
    private Integer cantidadStock = 0;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 5;

    @Column(name = "precio_unitario")
    private Double precioUnitario = 0.0;

    @Column(length = 100)
    private String ubicacion;

    public Repuesto() {}

    public Repuesto(String codigo, String nombre, String descripcion, String marca, Proveedor proveedor, Integer cantidadStock, Integer stockMinimo, Double precioUnitario, String ubicacion) {
        setCodigo(codigo);
        setNombre(nombre);
        setDescripcion(descripcion);
        this.marca = marca;
        this.proveedor = proveedor;
        this.cantidadStock = cantidadStock;
        this.stockMinimo = stockMinimo;
        this.precioUnitario = precioUnitario;
        setUbicacion(ubicacion);
    }

    public boolean isStockBajo() {
        return cantidadStock != null && stockMinimo != null && cantidadStock <= stockMinimo;
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
        if (nombre != null && nombre.length() > 35) {
            return nombre.substring(0, 35);
        }
        return nombre;
    }
    public void setNombre(String nombre) {
        if (nombre != null) {
            String trimmed = nombre.trim();
            this.nombre = trimmed.length() > 35 ? trimmed.substring(0, 35) : trimmed;
        } else {
            this.nombre = null;
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

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public Integer getCantidadStock() { return cantidadStock; }
    public void setCantidadStock(Integer cantidadStock) { this.cantidadStock = cantidadStock; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public String getUbicacion() {
        if (ubicacion != null && ubicacion.length() > 30) {
            return ubicacion.substring(0, 30);
        }
        return ubicacion;
    }
    public void setUbicacion(String ubicacion) {
        if (ubicacion != null) {
            String trimmed = ubicacion.trim();
            this.ubicacion = trimmed.length() > 30 ? trimmed.substring(0, 30) : trimmed;
        } else {
            this.ubicacion = null;
        }
    }
}
