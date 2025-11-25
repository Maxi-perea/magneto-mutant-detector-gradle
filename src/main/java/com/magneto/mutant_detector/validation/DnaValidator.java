package com.magneto.mutant_detector.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DnaValidator implements ConstraintValidator<ValidDna, String[]> {

    private static final int MAX_DNA_SIZE = 1000; // Protección Anti-DoS

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        if (dna == null) return false;

        int n = dna.length;
        if (n == 0) return false;

        // Validación de seguridad: Evitar matrices gigantes
        if (n > MAX_DNA_SIZE) {
            return false;
        }

        for (String row : dna) {
            // Validar NxN
            if (row == null || row.length() != n) return false;

            // Validar caracteres (Regex simple)
            if (!row.matches("[ATCG]+")) return false;
        }
        return true;
    }
}