package com.magneto.mutant_detector.service;

import com.magneto.mutant_detector.entity.DnaRecord;
import com.magneto.mutant_detector.repository.DnaRecordRepository;
import com.magneto.mutant_detector.dto.StatsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class MutantService {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MUTANT_THRESHOLD = 1;

    private final DnaRecordRepository dnaRepository;

    public MutantService(DnaRecordRepository dnaRepository) {
        this.dnaRepository = dnaRepository;
    }

    public boolean analyze(String[] dna) {
        // 1. Generar Hash único
        String dnaHash = Arrays.toString(dna);
        log.info("🔎 Analizando ADN con Hash: {}", dnaHash.hashCode());

        // 2. Verificar Caché (BD)
        Optional<DnaRecord> existingRecord = dnaRepository.findByDnaHash(dnaHash);
        if (existingRecord.isPresent()) {
            boolean result = existingRecord.get().isMutant();
            log.info("✅ ADN encontrado en base de datos. Resultado previo: {}", result ? "MUTANTE" : "HUMANO");
            return result;
        }

        // 3. Ejecutar algoritmo
        boolean isMutant = isMutantAlgorithm(dna);
        log.info("🧬 Análisis completado. Resultado: {}", isMutant ? "MUTANTE" : "HUMANO");

        // 4. Guardar
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

    // --- Algoritmo Core ---
    private boolean isMutantAlgorithm(String[] dna) {
        int n = dna.length;
        int sequenceCount = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j + SEQUENCE_LENGTH <= n) { // Horizontal
                    if (checkSequence(dna, i, j, 0, 1) && ++sequenceCount > MUTANT_THRESHOLD) return true;
                }
                if (i + SEQUENCE_LENGTH <= n) { // Vertical
                    if (checkSequence(dna, i, j, 1, 0) && ++sequenceCount > MUTANT_THRESHOLD) return true;
                }
                if (i + SEQUENCE_LENGTH <= n && j + SEQUENCE_LENGTH <= n) { // Diagonal Principal
                    if (checkSequence(dna, i, j, 1, 1) && ++sequenceCount > MUTANT_THRESHOLD) return true;
                }
                if (i + SEQUENCE_LENGTH <= n && j - SEQUENCE_LENGTH + 1 >= 0) { // Diagonal Inversa
                    if (checkSequence(dna, i, j, 1, -1) && ++sequenceCount > MUTANT_THRESHOLD) return true;
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
}