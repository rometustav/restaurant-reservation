package com.romet.restaurantreservation.repository;

import com.romet.restaurantreservation.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}