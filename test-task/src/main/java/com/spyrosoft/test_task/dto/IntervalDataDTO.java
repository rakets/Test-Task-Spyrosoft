package com.spyrosoft.test_task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntervalDataDTO {
    @JsonProperty("from")
    private ZonedDateTime fromDate;
    @JsonProperty("to")
    private ZonedDateTime toDate;
    private List<FuelDataDTO> generationmix;
}
