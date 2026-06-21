package com.spyrosoft.test_task.service;

import com.spyrosoft.test_task.dto.*;
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
                    double percentageValue = 0.0;
                    // situation if getPercent() return null (use 0.0)
                    if (fuel.getPerc() != null) {
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

    public OptimalWindowResponseDTO getOptimalChargingWindow (int hours) {
        // take forecast from current + 48h
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime end = now.plusDays(2);

        List<IntervalDataDTO> data = fetchEnergyData(now, end);

        // convert hours to 30min intervals Переводим часы в количество 30-минутных интервалов
        int intervalsNeeded = hours * 2;

        if (data == null || data.size() < intervalsNeeded) {
            throw new IllegalStateException("There ara not enough data from API.");
        }

        double maxCleanEnergyAvg = -1.0;
        int bestStartIndex = 0;

        // window algorithm
        for (int i = 0; i <= data.size() - intervalsNeeded; i++) {
            double windowCleanEnergySum = 0;

            // calculate clean energy into window
            for (int j = 0; j < intervalsNeeded; j++) {
                IntervalDataDTO interval = data.get(i + j);
                windowCleanEnergySum += calculateCleanEnergyForInterval(interval);
            }

            // average value for window
            double windowAvg = windowCleanEnergySum / intervalsNeeded;

            // save avg value, index of window, if window better then
            if (windowAvg > maxCleanEnergyAvg) {
                maxCleanEnergyAvg = windowAvg;
                bestStartIndex = i;
            }
        }

        // start interval
        IntervalDataDTO startInterval = data.get(bestStartIndex);
        // end interval
        IntervalDataDTO endInterval = data.get(bestStartIndex + intervalsNeeded - 1);

        OptimalWindowResponseDTO window = new OptimalWindowResponseDTO(startInterval.getFromDate(), endInterval.getToDate(), Math.round(maxCleanEnergyAvg * 100.0) / 100.0);
        return window;
    }

    // method calculate sum % of clean energy in 30min interval
    private double calculateCleanEnergyForInterval(IntervalDataDTO interval) {
        if (interval.getGenerationmix() == null) {
            return 0.0;
        }

        double cleanEnergySum = 0;
        for (FuelDataDTO fuel : interval.getGenerationmix()) {
            if (CLEAN_ENERGY.contains(fuel.getFuel())) {
                double percValue = 0.0;
                if (fuel.getPerc() != null) {
                    percValue = fuel.getPerc();
                }
                cleanEnergySum += percValue;
            }
        }
        return cleanEnergySum;
    }
}
