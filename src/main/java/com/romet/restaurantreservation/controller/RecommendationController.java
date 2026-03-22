package com.romet.restaurantreservation.controller;

import com.romet.restaurantreservation.model.RestaurantTable;
import com.romet.restaurantreservation.service.RecommendationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public List<RecommendationService.Recommendation> getRecommendations(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
            @RequestParam int partySize,
            @RequestParam(required = false) RestaurantTable.Zone zone,
            @RequestParam(defaultValue = "false") boolean windowSeat,
            @RequestParam(defaultValue = "false") boolean cornerSeat,
            @RequestParam(defaultValue = "false") boolean kidsAreaSeat
    ) {
        return recommendationService.recommend(date, startTime, endTime, partySize, zone, windowSeat, cornerSeat, kidsAreaSeat);
    }
}