package com.magneto.mutant_detector.service;

import com.magneto.mutant_detector.dto.StatsResponse;
import com.magneto.mutant_detector.entity.DnaRecord;
import com.magneto.mutant_detector.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
public class MutantService {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MUTANT_THRESHOLD = 1;

    private final DnaRecordRepository dnaRepository;

    public MutantService(DnaRecordRepository dnaRepository) {
        this.dnaRepository = dnaRepository;
    }

    /**
     * Método principal que orquesta la detección y persistencia.
     */
    public boolean analyze(String[] dna) {
        // 1. Validar ADN básico
        if (!isValidDna(dna)) {
            throw new IllegalArgumentException("ADN inválido");
        }

        // 2. Generar Hash único para este ADN (Optimización Nivel 3)
        // Usamos Arrays.toString como firma única simple y efectiva.
        String dnaHash = Arrays.toString(dna);

        // 3. Verificar si ya fue analizado en la BD (Evita re-procesar)
        Optional<DnaRecord> existingRecord = dnaRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            return existingRecord.get().isMutant();
        }

        // 4. Si es nuevo, ejecutamos el algoritmo
        boolean isMutant = isMutantAlgorithm(dna);

        // 5. Guardamos el resultado para el futuro
        DnaRecord newRecord = new DnaRecord(dnaHash, isMutant);
        dnaRepository.save(newRecord);

        return isMutant;
    }

    public StatsResponse getStats() {
        long countMutant = dnaRepository.countByIsMutant(true);
        long countHuman = dnaRepository.countByIsMutant(false);
        double ratio = countHuman == 0 ? 0 : (double) countMutant / countHuman;

        return new StatsResponse(countMutant, countHuman, ratio);
    }

    // --- Algoritmo Core (El mismo del Nivel 1, movido a método privado) ---
    private boolean isMutantAlgorithm(String[] dna) {
        int n = dna.length;
        int sequenceCount = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Horizontal
                if (j + SEQUENCE_LENGTH <= n) {
                    if (checkSequence(dna, i, j, 0, 1)) {
                        if (++sequenceCount > MUTANT_THRESHOLD) return true;
                    }
                }
                // Vertical
                if (i + SEQUENCE_LENGTH <= n) {
                    if (checkSequence(dna, i, j, 1, 0)) {
                        if (++sequenceCount > MUTANT_THRESHOLD) return true;
                    }
                }
                // Diagonal Principal
                if (i + SEQUENCE_LENGTH <= n && j + SEQUENCE_LENGTH <= n) {
                    if (checkSequence(dna, i, j, 1, 1)) {
                        if (++sequenceCount > MUTANT_THRESHOLD) return true;
                    }
                }
                // Diagonal Inversa
                if (i + SEQUENCE_LENGTH <= n && j - SEQUENCE_LENGTH + 1 >= 0) {
                    if (checkSequence(dna, i, j, 1, -1)) {
                        if (++sequenceCount > MUTANT_THRESHOLD) return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkSequence(String[] dna, int row, int col, int deltaRow, int deltaCol) {
        char base = dna[row].charAt(col);
        for (int k = 1; k < SEQUENCE_LENGTH; k++) {
            if (dna[row + k * deltaRow].charAt(col + k * deltaCol) != base) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidDna(String[] dna) {
        if (dna == null || dna.length == 0) return false;
        int n = dna.length;
        String validPattern = "[ATCG]+";
        for (String row : dna) {
            if (row == null || row.length() != n || !row.matches(validPattern)) return false;
        }
        return true;
    }
}