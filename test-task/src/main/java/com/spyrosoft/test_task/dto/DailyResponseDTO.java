package com.spyrosoft.test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyResponseDTO {
    private String date;
    private Map<String, Double> averageMix;
    private Double cleanEnergyPercent;
}
