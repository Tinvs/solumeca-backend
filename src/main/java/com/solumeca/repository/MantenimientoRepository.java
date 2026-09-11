package com.solumeca.repository;

import com.solumeca.model.Mantenimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findBySolicitanteOrderByFechaDesc(String solicitante);
    List<Mantenimiento> findAllByOrderByFechaDesc();
}
