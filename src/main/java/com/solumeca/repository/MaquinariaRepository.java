package com.solumeca.repository;

import com.solumeca.model.Maquinaria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MaquinariaRepository extends JpaRepository<Maquinaria, Long> {
    Optional<Maquinaria> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}
