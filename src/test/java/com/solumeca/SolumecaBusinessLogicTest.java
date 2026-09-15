package com.solumeca;

import com.solumeca.model.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SolumecaBusinessLogicTest {

    @Test
    void testMaquinariaCodigo() {
        Maquinaria maq = new Maquinaria("MQ-001", "Retroexcavadora 420F2", "CATERPILLAR", "420F2", "CAT-001", "Operativa");
        assertEquals("MQ-001", maq.getCodigo());
        assertEquals("CATERPILLAR", maq.getMarca());
        assertEquals("Operativa", maq.getEstado());
    }

    @Test
    void testRepuestoStockAlert() {
        Proveedor prov = new Proveedor("Distribuidora Diésel", "900.123.456-1", "Carlos", "3001234567", "carlos@test.com", "Calle 30", "Barranquilla", "Diésel");
        Repuesto repOk = new Repuesto("REP-001", "Filtro Aceite", "Filtro", "CATERPILLAR", prov, 15, 5, 145000.0, "Bodega A");
        assertFalse(repOk.isStockBajo(), "Stock con 15 unidades no debería considerarse bajo cuando el mínimo es 5");

        Repuesto repBajo = new Repuesto("REP-003", "Kit Sellos", "Sellos", "TOYOTA", prov, 3, 5, 280000.0, "Bodega B");
        assertTrue(repBajo.isStockBajo(), "Stock con 3 unidades debe alertar como bajo cuando el mínimo es 5");

        Repuesto repLimite = new Repuesto("REP-004", "Bomba Inyección", "Bomba", "CATERPILLAR", prov, 2, 2, 3450000.0, "Bodega A");
        assertTrue(repLimite.isStockBajo(), "Stock en el límite exacto (2 de 2) debe considerarse crítico");
    }

    @Test
    void testMarcaProveedorRelationship() {
        Proveedor prov = new Proveedor("Partes y Equipos", "800.789.123-4", "Mariana", "3012345678", "info@partes.com", "Cra 53", "Barranquilla", "Maquinaria");
        Marca marca = new Marca("KOMATSU", "Japón", "Excavadoras y cargadores", prov);

        assertEquals("KOMATSU", marca.getNombre());
        assertNotNull(marca.getProveedor());
        assertEquals("800.789.123-4", marca.getProveedor().getNit());
    }

    @Test
    void testMantenimientoFlujoPresolicitudAOrdenYFactura() {
        Mantenimiento m = new Mantenimiento();
        m.setMaquinariaId(1L);
        m.setSolicitante("cliente1");
        m.setTipo("Preventivo");
        m.setDescripcion("Mantenimiento de 250 horas");
        m.setEstado("Presolicitud");
        m.setFecha(LocalDate.now());

        assertEquals("Presolicitud", m.getEstado());
        assertNull(m.getNumeroOrden());

        // 1. Análisis técnico y cotización
        m.setAnalisis("Inspección visual y cambio preventivo de fluidos y filtros");
        m.setSolucion("Sustitución de aceite motor 15W40 y filtros primarios");
        m.setCostoEstimado(1200000.0);
        m.setDiasEstimados(2);
        m.setTecnicoAsignado("tecnico");
        m.setEstado("Presolicitud analizada");

        assertEquals("Presolicitud analizada", m.getEstado());
        assertEquals(1200000.0, m.getCostoEstimado());
        assertEquals(2, m.getDiasEstimados());

        // 2. Aprobación y generación de orden
        m.setEstado("Orden de trabajo");
        m.setNumeroOrden("ORD-2026-001");
        m.setFechaAprobacion(LocalDate.now());
        m.setValorTotal(m.getCostoEstimado());

        assertEquals("Orden de trabajo", m.getEstado());
        assertEquals("ORD-2026-001", m.getNumeroOrden());
        assertEquals(1200000.0, m.getValorTotal());

        // 3. Completado
        m.setEstado("Completado");
        assertEquals("Completado", m.getEstado());
    }
}

