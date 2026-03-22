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

    @Bean
    CommandLineRunner init(RestaurantTableRepository restaurantTableRepository, ReservationRepository reservationRepository) {
        return args -> {
            Random random = new Random();
            List<RestaurantTable> tables = restaurantTableRepository.findAll();

            List<String> customerNames = new ArrayList<>(List.of("Martin", "Anna", "Toomas", "Maria", "Urmas", "Kristina", "Aivar", "Sofia", "Artur", "Maarja"));

            // Loome suvaliselt 5-10 broneeringut tänase ja homse päeva peale
            int count = random.nextInt(6) + 5;
            for (int i = 0; i < count; i++) {
                RestaurantTable table = tables.get(random.nextInt(tables.size()));
                String name = customerNames.remove(random.nextInt(customerNames.size()));
                int startTime = random.nextInt(11) + 10; // Oletame, et restoran on avatud 10-21 (hiliseim broneering on algusajaga 20)
                int endTime = Math.min(startTime + random.nextInt(3) + 1, 21); // Broneering saab kesta 1-3h (kuid mitte lõppeda peale 21)

                Reservation r = new  Reservation();
                r.setName(name);
                r.setDate(LocalDate.now().plusDays(random.nextInt(2)));
                r.setStartTime(LocalTime.of(startTime, 0));
                r.setEndTime(LocalTime.of(endTime, 0));
                r.setPartySize(random.nextInt(table.getCapacity()) + 1);
                r.setTable(table);
                reservationRepository.save(r);
            }
        };
    }

}
