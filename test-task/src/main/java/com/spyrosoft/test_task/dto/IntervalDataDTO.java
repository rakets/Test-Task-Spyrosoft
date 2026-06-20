package com.spyrosoft.test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalDataDTO {
    private ZonedDateTime fromDate;
    private ZonedDateTime toDate;
    private List<FuelDataDTO> generationmix;
}
