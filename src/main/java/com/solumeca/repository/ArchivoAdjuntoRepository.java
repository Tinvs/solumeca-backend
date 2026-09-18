package com.solumeca.repository;

import com.solumeca.model.ArchivoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArchivoAdjuntoRepository extends JpaRepository<ArchivoAdjunto, Long> {
    Optional<ArchivoAdjunto> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
