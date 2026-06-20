package com.spyrosoft.test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuelDataDTO {
    private String fuel;
    private double percent;
}
