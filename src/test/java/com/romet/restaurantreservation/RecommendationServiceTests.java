package com.romet.restaurantreservation;

import com.romet.restaurantreservation.model.Reservation;
import com.romet.restaurantreservation.model.RestaurantTable;
import com.romet.restaurantreservation.repository.ReservationRepository;
import com.romet.restaurantreservation.repository.RestaurantTableRepository;
import com.romet.restaurantreservation.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTests {

    @Mock
    private RestaurantTableRepository tableRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private RecommendationService recommendationService;

    private RestaurantTable table2seat;
    private RestaurantTable table4seat;
    private RestaurantTable table6seat;
    private RestaurantTable table8seat;
    private RestaurantTable tableWindow;
    private RestaurantTable tableCorner;
    private RestaurantTable tableKidsArea;
    private RestaurantTable tableTerrace;

    private final LocalDate date = LocalDate.of(2026, 3, 22);
    private final LocalTime start = LocalTime.of(18, 0);
    private final LocalTime end = LocalTime.of(20, 0);

    @BeforeEach
    void setUp() {
        table2seat = createTable(1L, 2, RestaurantTable.Zone.INDOOR, 1, 1, false, false, false);
        table4seat = createTable(2L, 4, RestaurantTable.Zone.INDOOR, 3, 1, false, false, false);
        table6seat = createTable(3L, 6, RestaurantTable.Zone.INDOOR, 6, 1, false, false, false);
        table8seat = createTable(4L, 8, RestaurantTable.Zone.INDOOR, 9, 1, false, false, false);

        tableCorner = createTable(5L, 4, RestaurantTable.Zone.INDOOR, 12, 1, true, false, false);
        tableWindow = createTable(6L, 4, RestaurantTable.Zone.INDOOR, 15, 1, false, true, false);
        tableKidsArea = createTable(7L, 4, RestaurantTable.Zone.INDOOR, 18, 1, false, false, true);

        tableTerrace = createTable(8L, 4, RestaurantTable.Zone.TERRACE, 21, 1, false, false, false);
    }

    // Test 1 (8tk): Algoritm peaks soovitama kõige väiksemat sobivat lauda, arvestades seltskonna suurust
    @ParameterizedTest
    @CsvSource({"1, 2", "2, 2", "3, 4", "4, 4", "5, 6", "6, 6", "7, 8", "8, 8"})
    void shouldPreferSmallestFittingTable(int guests, int expectedCapacity) {
        when(tableRepository.findAll()).thenReturn(List.of(table2seat, table4seat, table6seat, table8seat));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, guests, null, false, false, false);

        assertFalse(results.isEmpty());
        assertEquals(expectedCapacity, results.getFirst().tables().getFirst().getCapacity());
    }

    // Test 2: Aknaäärse laua skoor peaks tõusma, kui see on eelistus
    @Test
    void windowPreferenceShouldBoostScore() {
        when(tableRepository.findAll()).thenReturn(List.of(table4seat, tableWindow));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, true, false, false);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isWindowTable());
    }

    // Test 3: Nurgas oleva laua skoor peaks tõusma, kui see on eelistus
    @Test
    void cornerPreferenceShouldBoostScore() {
        when(tableRepository.findAll()).thenReturn(List.of(table4seat, tableCorner));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, false, true, false);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isCornerTable());
    }

    // Test 4: Laste ala lähedal asuva laua skoor peaks tõusma, kui see on eelistus
    @Test
    void kidsAreaPreferenceShouldBoostScore() {
        when(tableRepository.findAll()).thenReturn(List.of(table4seat, tableKidsArea));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, false, false, true);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isKidsAreaTable());
    }

    // Test 5: Laste ala lähedal asuva laua skoor peaks olema suurem aknaäärse laua skoorist, kui eelistuses on peale laste ala ka aknaäärne laud
    @Test
    void kidsAreaPreferenceShouldBoostScoreMoreThanWindowPreference() {
        when(tableRepository.findAll()).thenReturn(List.of(tableWindow, tableKidsArea));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, true, false, true);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isKidsAreaTable());
    }

    // Test 6: Laste ala lähedal asuva laua skoor peaks olema suurem nurgas oleva laua skoorist, kui eelistuses on peale laste ala ka nurgas olev laud
    @Test
    void kidsAreaPreferenceShouldBoostScoreMoreThanCornerPreference() {
        when(tableRepository.findAll()).thenReturn(List.of(tableCorner, tableKidsArea));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, false, true, true);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isKidsAreaTable());
    }

    // Test 7: Laste ala lähedal asuva laua skoor peaks olema kõige suurem, kui kõik eelistused on kaasatud
    @Test
    void kidsAreaPreferenceShouldBoostScoreMoreThanAnyOtherPreference() {
        when(tableRepository.findAll()).thenReturn(List.of(tableWindow, tableCorner, tableKidsArea));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, null, true, true, true);

        assertFalse(results.isEmpty());
        assertTrue(results.getFirst().tables().getFirst().isKidsAreaTable());
    }

    // Test 8: Tsooni valik peaks olema kõrgema prioriteediga kui laste ala lähedal oleva laua eelistus
    @Test
    void zoneShouldBePrioritizedOverKidsAreaPreference() {
        when(tableRepository.findAll()).thenReturn(List.of(tableTerrace, tableKidsArea));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 4, RestaurantTable.Zone.TERRACE, false, false, true);

        assertFalse(results.isEmpty());
        assertEquals(RestaurantTable.Zone.TERRACE, results.getFirst().tables().getFirst().getZone());
    }

    // Test 9: Kui laud on broneeritud sellel ajal, siis ei tohiks teda soovituste hulka lisada
    @Test
    void occupiedTableShouldNotBeRecommended() {
        Reservation reservation = new Reservation();
        reservation.setTable(table2seat);
        reservation.setDate(date);
        reservation.setStartTime(LocalTime.of(17, 0));
        reservation.setEndTime(LocalTime.of(19, 0));

        when(tableRepository.findAll()).thenReturn(List.of(table2seat, table4seat));
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 2, null, false, false, false);

        assertFalse(results.isEmpty());
        assertTrue(results.stream().noneMatch(r -> r.tables().stream().anyMatch(t -> t.getId().equals(table2seat.getId()))));
    }

    // Test 10: Kui broneering algab täpselt samal ajal laua eelneva broneeringuga, siis peaks selle laua soovituste hulka lisama
    @Test
    void tableShouldBeAvailableWhenPreviousReservationEndsAtStartTime() {
        Reservation reservation = new Reservation();
        reservation.setTable(table2seat);
        reservation.setDate(date);
        reservation.setStartTime(LocalTime.of(16, 0));
        reservation.setEndTime(LocalTime.of(18, 0));

        when(tableRepository.findAll()).thenReturn(List.of(table2seat, table4seat));
        when(reservationRepository.findAll()).thenReturn(List.of(reservation));

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 2, null, false, false, false);

        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.tables().stream().anyMatch(t -> t.getId().equals(table2seat.getId()))));
    }

    // Test 11: Algoritm peaks tagastama tühja soovituste listi, kui ei ole ühtegi lauda saadaval
    @Test
    void shouldReturnEmptyWhenNoTablesAvailable() {
        Reservation r1 = createReservation(table2seat);
        Reservation r2 = createReservation(table4seat);

        when(tableRepository.findAll()).thenReturn(List.of(table2seat, table4seat));
        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 2, null, false, false, false);

        assertTrue(results.isEmpty());
    }

    // Test 12: Kui seltskond ei mahu ühte lauda, peaks algoritm laudu kokku lükkama
    @Test
    void shouldMergeTablesWhenNoSingleTableFits() {
        // Kaks 4 kohalist kõrvuti asuvat lauda (4 kohalised lauad on 2x2 maatriks)
        RestaurantTable t1 = createTable(9L, 4, RestaurantTable.Zone.INDOOR, 1, 1, false, false, false); // Esimene laud x[1,2] y[1,2]
        RestaurantTable t2 = createTable(10L, 4, RestaurantTable.Zone.INDOOR, 4, 1, false, false, false); // Teine laud x[4,5] y[1,2]

        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 7, null, false, false, false);

        assertFalse(results.isEmpty());
        assertEquals(2, results.getFirst().tables().size());
    }

    // Test 13: Erinevatest aladest ei tohiks laudu kokku lükata
    @Test
    void shouldNotMergeTablesFromDifferentZones() {
        RestaurantTable indoorTable = createTable(11L, 4, RestaurantTable.Zone.INDOOR, 1, 1, false, false, false);
        RestaurantTable terraceTable = createTable(12L, 4, RestaurantTable.Zone.TERRACE, 4, 1, false, false, false);

        when(tableRepository.findAll()).thenReturn(List.of(indoorTable, terraceTable));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 7, null, false, false, false);

        assertTrue(results.isEmpty());
    }

    // Test 14: Sobiva üksiku laua olemasolul ei tohiks laudu kokku lükata
    @Test
    void shouldNotMergeTablesWhenSingleTableFits() {
        RestaurantTable singleTable = createTable(13L, 8, RestaurantTable.Zone.INDOOR, 1, 1, false, false, false);
        RestaurantTable t1 = createTable(14L, 4, RestaurantTable.Zone.INDOOR, 4, 1, false, false, false);
        RestaurantTable t2 = createTable(15L, 4, RestaurantTable.Zone.INDOOR, 7, 1, false, false, false);

        when(tableRepository.findAll()).thenReturn(List.of(singleTable, t1, t2));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 7, null, false, false, false);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(8, results.getFirst().tables().getFirst().getCapacity());
    }

    // Test 15: Laudu ei tohiks kokku lükata, kui nad ei asu kõrvuti
    @Test
    void shouldNotMergeDistantTables() {
        RestaurantTable t1 = createTable(16L, 4, RestaurantTable.Zone.INDOOR, 1, 1, false, false, false);
        RestaurantTable t2 = createTable(17L, 4, RestaurantTable.Zone.INDOOR, 10, 10, false, false, false);

        when(tableRepository.findAll()).thenReturn(List.of(t1, t2));
        when(reservationRepository.findAll()).thenReturn(List.of());

        List<RecommendationService.Recommendation> results = recommendationService.recommend(date, start, end, 7, null, false, false, false);

        assertTrue(results.isEmpty());
    }

    // Abimeetodid testide jaoks

    private RestaurantTable createTable(Long id, int capacity, RestaurantTable.Zone zone, int x, int y, boolean corner, boolean window, boolean kids) {
        RestaurantTable table = new RestaurantTable();
        table.setId(id);
        table.setCapacity(capacity);
        table.setZone(zone);
        table.setX(x);
        table.setY(y);
        table.setCornerTable(corner);
        table.setWindowTable(window);
        table.setKidsAreaTable(kids);
        return table;
    }

    private Reservation createReservation(RestaurantTable table) {
        Reservation r = new Reservation();
        r.setTable(table);
        r.setDate(date);
        r.setStartTime(LocalTime.of(17, 0));
        r.setEndTime(LocalTime.of(19, 0));
        return r;
    }
}