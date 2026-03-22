package com.romet.restaurantreservation.controller;

import com.romet.restaurantreservation.model.RestaurantTable;
import com.romet.restaurantreservation.repository.RestaurantTableRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final RestaurantTableRepository tableRepository;

    public TableController(RestaurantTableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping
    public List<RestaurantTable> getAllTables() {
        return tableRepository.findAll();
    }
}