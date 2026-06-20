package com.spyrosoft.test_task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonApiResponseDTO {
    private List<IntervalDataDTO> data;
}

