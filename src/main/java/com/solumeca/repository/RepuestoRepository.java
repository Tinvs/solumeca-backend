package com.solumeca.repository;

import com.solumeca.model.Repuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RepuestoRepository extends JpaRepository<Repuesto, Long> {
    Optional<Repuesto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    List<Repuesto> findAllByOrderByNombreAsc();
}

