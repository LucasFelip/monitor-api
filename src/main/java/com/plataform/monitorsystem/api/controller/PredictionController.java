package com.plataform.monitorsystem.api.controller;

import com.plataform.monitorsystem.domain.service.PredictionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {
    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping
    public ResponseEntity<String> predictAnomalyRisk() {
        String predictionMessage = predictionService.predictAnomalyRisk();
        return ResponseEntity.ok(predictionMessage);
    }
}
