package com.magneto.mutant_detector.repository;

import com.magneto.mutant_detector.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    // Buscar por Hash (para verificar si ya existe)
    Optional<DnaRecord> findByDnaHash(String dnaHash);

    // Contar mutantes o humanos (para las stats)
    long countByIsMutant(boolean isMutant);
}