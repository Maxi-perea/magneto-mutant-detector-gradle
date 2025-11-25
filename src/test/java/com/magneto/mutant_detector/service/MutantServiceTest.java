package com.magneto.mutant_detector.service;

import com.magneto.mutant_detector.entity.DnaRecord;
import com.magneto.mutant_detector.repository.DnaRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MutantServiceTest {

    @Mock
    private DnaRecordRepository dnaRepository;

    @InjectMocks
    private MutantService mutantService;

    @Test
    void testIsMutant_Horizontal() {
        String[] dna = {
                "AAAA", // Secuencia 1
                "CCCC", // Secuencia 2
                "CAGT",
                "CAGT"
        };
        when(dnaRepository.findByDnaHash(any())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyze(dna));
    }

    @Test
    void testIsMutant_Vertical() {
        String[] dna = {"ACGT", "ACGT", "ACGT", "ACGT"}; // 4 A verticales y otras coincidencias
        when(dnaRepository.findByDnaHash(any())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyze(dna));
    }

    @Test
    void testIsMutant_Diagonal() {
        // Este ADN tiene 2 secuencias:
        // 1. Horizontal en la fila 0: "AAAA"
        // 2. Diagonal Principal: "A", "A", "A", "A" (posiciones 0,0 a 3,3)
        String[] dna = {
                "AAAA", // <-- Secuencia 1 (Horizontal)
                "CAGT",
                "TCAT",
                "GTCA"  // <-- Secuencia 2 (Diagonal se forma con las 'A' de arriba)
        };

        when(dnaRepository.findByDnaHash(any())).thenReturn(Optional.empty());

        assertTrue(mutantService.analyze(dna));
    }

    @Test
    void testIsHuman() {
        String[] dna = {"ATGC", "CAGT", "TTAT", "AGAC"}; // Sin secuencias suficientes
        when(dnaRepository.findByDnaHash(any())).thenReturn(Optional.empty());

        assertFalse(mutantService.analyze(dna));
    }

    @Test
    void testCache_ExistingRecord() {
        String[] dna = {"AAAA", "AAAA", "AAAA", "AAAA"};
        // Simulamos que ya existe en BD como Mutante
        when(dnaRepository.findByDnaHash(any())).thenReturn(Optional.of(new DnaRecord("hash", true)));

        assertTrue(mutantService.analyze(dna));
        // Verificamos que NO se llamó a save (ahorro de recursos)
        verify(dnaRepository, never()).save(any());
    }
}