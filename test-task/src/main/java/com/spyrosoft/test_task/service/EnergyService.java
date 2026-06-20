package com.spyrosoft.test_task.service;

import com.spyrosoft.test_task.dto.CarbonApiResponseDTO;
import com.spyrosoft.test_task.dto.DailyResponseDTO;
import com.spyrosoft.test_task.dto.FuelDataDTO;
import com.spyrosoft.test_task.dto.IntervalDataDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class EnergyService {
    private final RestClient restClient;

    private final Set<String> CLEAN_ENERGY = Set.of(
            "biomass", "nuclear", "hydro", "wind", "solar"
    );

    public EnergyService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.carbonintensity.org.uk")
                .build();
    }

    // method for getting mix from 3 days energy data
    public List<DailyResponseDTO> getThreeDaysEnergyMix() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = now.plusDays(2);

        List<IntervalDataDTO> dataList = fetchEnergyData(now, end);

        // 30min intervals group by date
        Map<LocalDate, List<IntervalDataDTO>> groupedByDay = new HashMap<>();
        for (IntervalDataDTO dataInterval : dataList) {
            LocalDate date = dataInterval.getFromDate().toLocalDate();
            // check if list don't contain date
            if (!groupedByDay.containsKey(date)) {
                groupedByDay.put(date, new ArrayList<>());
            }
            groupedByDay.get(date).add(dataInterval);
        }
        List<DailyResponseDTO> response = new ArrayList<>();
        for (Map.Entry<LocalDate, List<IntervalDataDTO>> entry : groupedByDay.entrySet()) {
            DailyResponseDTO dailyAverage = calculateDailyAverage(entry.getKey(), entry.getValue());
            response.add(dailyAverage);
        }
        response.sort(Comparator.comparing(DailyResponseDTO::getDate));
        return response;
    }

    // method to take date (from, to) and create intervals list
    private List<IntervalDataDTO> fetchEnergyData(ZonedDateTime from, ZonedDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm'Z'");

        // remove sec
        String fromStr = from.truncatedTo(ChronoUnit.MINUTES).format(formatter);
        String toStr = to.truncatedTo(ChronoUnit.MINUTES).format(formatter);

        String url = "https://api.carbonintensity.org.uk/generation/" + fromStr + "/" + toStr;

        CarbonApiResponseDTO response = restClient.get()
                .uri(URI.create(url))
                .retrieve()
                .body(CarbonApiResponseDTO.class);

        if (response != null && response.getData() != null) {
            return response.getData();
        } else {
            return Collections.emptyList();
        }
    }

    // method for collecting and getting average values for one day
    private DailyResponseDTO calculateDailyAverage(LocalDate date, List<IntervalDataDTO> intervals) {
        Map<String, Double> fuelSumMap = new HashMap<>();
        int totalIntervals = intervals.size();

        // sum percent for every fuel type in a day
        for (IntervalDataDTO interval : intervals) {
            if (interval.getGenerationmix() != null) {
                for (FuelDataDTO fuel : interval.getGenerationmix()) {
                    double percentageValue;
                    // situation if getPercent() return null (use 0.0)
                    if (fuel.getPerc() == null) {
                        percentageValue = 0.0;
                    } else {
                        percentageValue = fuel.getPerc();
                    }
                    // use variable 'percentageValue'
                    if (fuelSumMap.containsKey(fuel.getFuel())) {
                        double sum = fuelSumMap.get(fuel.getFuel());
                        fuelSumMap.put(fuel.getFuel(), sum + percentageValue);
                    } else {
                        fuelSumMap.put(fuel.getFuel(), percentageValue);
                    }
                }
            }
        }

        Map<String, Double> averageMix = new HashMap<>();
        double cleanEnergyTotalAvg = 0;

        // average for every fuel type
        for (Map.Entry<String, Double> entry : fuelSumMap.entrySet()) {
            double average = entry.getValue() / totalIntervals;
            averageMix.put(entry.getKey(), Math.round(average * 100.0) / 100.0);

            // if CLEAN_ENERGY contain source
            if (CLEAN_ENERGY.contains(entry.getKey())) {
                cleanEnergyTotalAvg += average;
            }
        }
        DailyResponseDTO responseDTO = new DailyResponseDTO(date.toString(), averageMix,Math.round(cleanEnergyTotalAvg * 100.0) / 100.0);
        return responseDTO;
    }
}
