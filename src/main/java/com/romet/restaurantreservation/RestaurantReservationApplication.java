package com.romet.restaurantreservation;

import com.romet.restaurantreservation.model.Reservation;
import com.romet.restaurantreservation.model.RestaurantTable;
import com.romet.restaurantreservation.repository.ReservationRepository;
import com.romet.restaurantreservation.repository.RestaurantTableRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class RestaurantReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantReservationApplication.class, args);
    }

}
