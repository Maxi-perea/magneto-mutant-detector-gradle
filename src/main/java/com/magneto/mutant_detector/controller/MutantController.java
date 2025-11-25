package com.magneto.mutant_detector.controller;

import com.magneto.mutant_detector.dto.DnaRequest;
import com.magneto.mutant_detector.dto.StatsResponse;
import com.magneto.mutant_detector.service.MutantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class MutantController {

    private final MutantService mutantService;

    public MutantController(MutantService mutantService) {
        this.mutantService = mutantService;
    }

    @Operation(summary = "Detectar si un humano es mutante", description = "Analiza la secuencia de ADN enviada y determina si cumple con las condiciones para ser mutante.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Es un Mutante"),
            @ApiResponse(responseCode = "403", description = "Es un Humano"),
            @ApiResponse(responseCode = "400", description = "ADN Inválido")
    })
    @PostMapping("/mutant/")
    public ResponseEntity<Void> detectMutant(@Valid @RequestBody DnaRequest dnaRequest) {
        boolean isMutant = mutantService.analyze(dnaRequest.getDna());
        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats() {
        return ResponseEntity.ok(mutantService.getStats());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}