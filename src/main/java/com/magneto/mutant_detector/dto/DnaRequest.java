package com.magneto.mutant_detector.dto;

import com.magneto.mutant_detector.validation.ValidDna;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DnaRequest {
    @ValidDna
    private String[] dna;
}
