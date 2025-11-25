package com.magneto.mutant_detector.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "dna_records", indexes = {
        @Index(name = "idx_dna_hash", columnList = "dnaHash") // Índice para búsquedas rápidas
})
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String dnaHash; // Guardamos el HASH, no el array gigante

    @Column(nullable = false)
    private boolean isMutant;

    private LocalDateTime createdAt;

    // Constructor personalizado
    public DnaRecord(String dnaHash, boolean isMutant) {
        this.dnaHash = dnaHash;
        this.isMutant = isMutant;
        this.createdAt = LocalDateTime.now();
    }
}