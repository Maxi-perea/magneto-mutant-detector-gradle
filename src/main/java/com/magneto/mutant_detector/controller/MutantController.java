package com.magneto.mutant_detector.controller;

import com.magneto.mutant_detector.dto.DnaRequest;
import com.magneto.mutant_detector.dto.StatsResponse;
import com.magneto.mutant_detector.service.MutantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MutantController {

    private final MutantService mutantService;

    public MutantController(MutantService mutantService) {
        this.mutantService = mutantService;
    }

    @PostMapping("/mutant/")
    public ResponseEntity<Void> detectMutant(@Valid @RequestBody DnaRequest dnaRequest) {
        try {
            // Usamos 'analyze' que incluye la lógica de BD
            boolean isMutant = mutantService.analyze(dnaRequest.getDna());

            if (isMutant) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(mutantService.getStats());
    }
}