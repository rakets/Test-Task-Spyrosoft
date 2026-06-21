package com.spyrosoft.test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptimalWindowResponseDTO {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private double avgCleanEnergyPercentage;
}
