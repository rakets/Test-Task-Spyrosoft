package com.spyrosoft.test_task.controller;

import com.spyrosoft.test_task.dto.DailyResponseDTO;
import com.spyrosoft.test_task.dto.OptimalWindowResponseDTO;
import com.spyrosoft.test_task.service.EnergyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/mix")
    public ResponseEntity<List<DailyResponseDTO>> getEnergyMix() {
        List<DailyResponseDTO> response = energyService.getThreeDaysEnergyMix();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/optimal-window/{hours}")
    public ResponseEntity<OptimalWindowResponseDTO> getOptimalWindow(@PathVariable("hours") int hours) {
        if (hours < 1 || hours > 6) {
            return ResponseEntity.badRequest().build();
        }
        OptimalWindowResponseDTO response = energyService.getOptimalChargingWindow(hours);
        return ResponseEntity.ok(response);
    }
}
