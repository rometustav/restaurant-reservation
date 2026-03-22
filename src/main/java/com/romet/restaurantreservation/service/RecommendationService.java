package com.romet.restaurantreservation.service;

import com.romet.restaurantreservation.model.Reservation;
import com.romet.restaurantreservation.model.RestaurantTable;
import com.romet.restaurantreservation.repository.ReservationRepository;
import com.romet.restaurantreservation.repository.RestaurantTableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final RestaurantTableRepository tableRepository;
    private final ReservationRepository reservationRepository;

    public RecommendationService(RestaurantTableRepository tableRepository, ReservationRepository reservationRepository) {
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Soovitab parimaid laudu arvestades seltskonna suurust ja eelistusi.
     * Tagastab sorteeritud nimekirja, kus esimene element on parima soovitusskooriga laud.
     * Kui ei ole ühtegi üksikut lauda, mis mahutaks seltskonna ära, siis otsib algoritm kõrvuti
     * asuvaid vabu laudu, mida saaks kokku lükata.
     *
     * @param date broneeringu kuupäev
     * @param startTime broneeringu algus
     * @param endTime broneeringu lõpp
     * @param partySize seltskonna suurus
     * @param zone soovitud tsoon (võib olla null kui pole eelistust)
     * @param windowSeat kas soovitakse aknaäärset lauda
     * @param cornerSeat kas soovitakse (vaikses) nurgas lauda
     * @param kidsAreaSeat kas soovitakse laste mängunurga lähedal lauda
     * @return sorteeritud list soovitustest (parimast halvimani)
     */
    public List<Recommendation> recommend(LocalDate date, LocalTime startTime, LocalTime endTime, int partySize, RestaurantTable.Zone zone, boolean windowSeat, boolean cornerSeat, boolean kidsAreaSeat) {

        // Lauad, mis on soovitud ajal vabad
        List<RestaurantTable> freeTables = getFreeTables(date, startTime, endTime);

        List<Recommendation> recommendations = new ArrayList<>();

        // Algul proovime kalkuleerida skoore üksikutele laudadele
        for (RestaurantTable table : freeTables) {
            if (table.getCapacity() >= partySize) {
                int score = calculateScore(table, partySize, zone, windowSeat, cornerSeat, kidsAreaSeat);
                recommendations.add(new Recommendation(List.of(table), score));
            }
        }

        // Kui ei ole üksikut lauda, mis mahutaks seltskonna ära, siis kalkuleerime skoori liidetud laudadele
        int maxCapacity = freeTables.stream().mapToInt(RestaurantTable::getCapacity).max().orElse(0);

        if (partySize > maxCapacity) {
            List<Recommendation> mergedTables = findMergedTables(freeTables, partySize, zone, windowSeat, cornerSeat, kidsAreaSeat);
            recommendations.addAll(mergedTables);
        }

        // Sorteerime parima skoori järgi ning tagastame sorteeritud listi
        recommendations.sort((a, b) -> b.score() - a.score());
        return recommendations;
    }

    /**
     * Leiab kõik lauad, mis on antud ajavahemikul vabad.
     * Laud loetakse hõivatuks, kui tal on broneering mis kattub antud ajavahemikuga.
     */
    private List<RestaurantTable> getFreeTables(LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<RestaurantTable> allTables = tableRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAll();

        return allTables.stream()
                .filter(table -> reservations.stream().noneMatch(r ->
                        r.getTable().getId().equals(table.getId())
                                && r.getDate().equals(date)
                                && r.getStartTime().isBefore(endTime)
                                && r.getEndTime().isAfter(startTime)
                )).collect(Collectors.toList());
    }

    /**
     * Arvutab sobivuse skoori igale lauale (algskoor on 100).
     * Tsooni klappivus on suurim faktor: klappivus suurendab skoori +35, puuduvus vähendab +25.
     * Iga tühi koht vähendab skoori -15 (efektiivsuse tagamiseks).
     * Iga klappiv eelistus suurendab skoori +25, iga puuduv soovitud eelistus vähendab skoori -15.
     * Erandiks laste mängunurga läheduses olevad lauad, kus klappivus suurendab +30 ning puuduvus vähendab -18
     * (kuna tõenäoliselt on see veidi tähtsam eelistus).
     */
    private int calculateScore(RestaurantTable table, int partySize, RestaurantTable.Zone zone, boolean windowSeat, boolean cornerSeat, boolean kidsAreaSeat) {
        int score = 100;

        if (zone != null && table.getZone() == zone) score += 35;
        if (zone != null && table.getZone() != zone) score -= 20;

        int emptySeats = table.getCapacity() - partySize;
        score -= emptySeats * 15;

        if (windowSeat && table.isWindowSeat()) score += 25;
        if (cornerSeat && table.isCornerSeat()) score += 25;
        if (kidsAreaSeat && table.isKidsAreaSeat()) score += 30;

        if (windowSeat && !table.isWindowSeat()) score -= 15;
        if (cornerSeat && !table.isCornerSeat()) score -= 15;
        if (kidsAreaSeat && !table.isKidsAreaSeat()) score -= 18;

        return score;
    }

    /**
     * Otsib kõrvuti asuvaid vabu laudu, mida saab kokku lükata.
     * Liidetud laudadele skoori ei vähendata, sest neid soovitab
     * algoritm alles siis, kui sobivaid üksikuid laudu pole.
     */
    private List<Recommendation> findMergedTables(List<RestaurantTable> freeTables, int partySize, RestaurantTable.Zone zone, boolean windowSeat, boolean cornerSeat, boolean kidsAreaSeat) {
        List<Recommendation> merged = new ArrayList<>();

        for (int i = 0; i < freeTables.size(); i++) {
            for (int j = i + 1; j < freeTables.size(); j++) {
                RestaurantTable t1 = freeTables.get(i);
                RestaurantTable t2 = freeTables.get(j);

                if (!areAdjacent(t1, t2)) continue;

                int totalCapacity = t1.getCapacity() + t2.getCapacity();
                if (totalCapacity < partySize) continue;

                int combinedScore = 100;

                // Tühjade kohtade karistus kogu liidetud laua peale
                int emptySeats = totalCapacity - partySize;
                combinedScore -= emptySeats * 15;

                // Tsooni klappivus (areAdjacent juba kontrollib, et mõlemad lauad on samas tsoonis)
                if (zone != null && t1.getZone() == zone) combinedScore += 35;
                if (zone != null && t1.getZone() != zone) combinedScore -= 20;

                // Eelistused - piisab kui vähemalt ühel laual on
                if (windowSeat && (t1.isWindowSeat() || t2.isWindowSeat())) combinedScore += 25;
                if (windowSeat && !t1.isWindowSeat() && !t2.isWindowSeat()) combinedScore -= 15;
                if (cornerSeat && (t1.isCornerSeat() || t2.isCornerSeat())) combinedScore += 25;
                if (cornerSeat && !t1.isCornerSeat() && !t2.isCornerSeat()) combinedScore -= 15;
                if (kidsAreaSeat && (t1.isKidsAreaSeat() || t2.isKidsAreaSeat())) combinedScore += 30;
                if (kidsAreaSeat && !t1.isKidsAreaSeat() && !t2.isKidsAreaSeat()) combinedScore -= 18;

                merged.add(new Recommendation(List.of(t1, t2), combinedScore));
            }
        }

        return merged;
    }

    /**
     * Kontrollib, kas kaks lauda on kõrvuti ja samas tsoonis.
     * Arvestab laudade tegelike mõõtmetega (2x1, 2x2, 3x2, 4x2).
     * Lauad on kõrvuti, kui nende vahel on max 1 ruut tühja ruumi.
     */
    private boolean areAdjacent(RestaurantTable t1, RestaurantTable t2) {
        if (t1.getZone() != t2.getZone()) return false;

        int t1Right = t1.getX() + t1.getWidth();
        int t1Bottom = t1.getY() + t1.getHeight();
        int t2Right = t2.getX() + t2.getWidth();
        int t2Bottom = t2.getY() + t2.getHeight();

        // Horisontaalselt kõrvuti: y-vahemikud kattuvad ja x-vahe <= 1
        boolean yOverlap = t1.getY() < t2Bottom && t2.getY() < t1Bottom;
        boolean xClose = Math.abs(t1Right - t2.getX()) <= 1 || Math.abs(t2Right - t1.getX()) <= 1;
        boolean horizontallyAdjacent = yOverlap && xClose;

        // Vertikaalselt kõrvuti: x-vahemikud kattuvad ja y-vahe <= 1
        boolean xOverlap = t1.getX() < t2Right && t2.getX() < t1Right;
        boolean yClose = Math.abs(t1Bottom - t2.getY()) <= 1 || Math.abs(t2Bottom - t1.getY()) <= 1;
        boolean verticallyAdjacent = xOverlap && yClose;

        return horizontallyAdjacent || verticallyAdjacent;
    }

    // Soovituse tulemus - sisaldab soovitatud lauda/laudu ja sobivuse skoori
    public record Recommendation(List<RestaurantTable> tables, int score) {}
}