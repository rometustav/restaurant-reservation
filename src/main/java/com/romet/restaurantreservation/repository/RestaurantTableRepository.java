package com.romet.restaurantreservation.repository;

import com.romet.restaurantreservation.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {
}
