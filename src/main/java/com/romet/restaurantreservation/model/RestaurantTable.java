package com.romet.restaurantreservation.model;

import jakarta.persistence.*;

@Entity
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private int capacity;

    public enum Zone {
        TERRACE,
        INDOOR,
        PRIVATE,
    }
    @Enumerated(EnumType.STRING)
    private Zone zone;

    // Laua ülemise vasakpoolse nurga positsioon
    private int x;
    private int y;

    private boolean cornerTable;
    private boolean windowTable;
    private boolean kidsAreaTable;

    public RestaurantTable() {}

    /**
     Laudade paigutuse jaoks abifunktsioonid. Olenevalt laua
     mahutavusest, näevad lauad maatriksitena välja sellised:
     Mahutavus 2 -> 2x1
     Mahutavus 4 -> 2x2
     Mahutavus 6 -> 3x2
     Mahutavus 8 -> 4x2
     */
    public int getWidth() {
        return switch (capacity) {
            case 6 -> 3;
            case 8 -> 4;
            default -> 2;
        };
    }

    public int getHeight() {
        if (capacity == 2) return 1;
        return 2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isCornerTable() {
        return cornerTable;
    }

    public void setCornerTable(boolean cornerSeat) {
        this.cornerTable = cornerSeat;
    }

    public boolean isWindowTable() {
        return windowTable;
    }

    public void setWindowTable(boolean windowSeat) {
        this.windowTable = windowSeat;
    }

    public boolean isKidsAreaTable() {
        return kidsAreaTable;
    }

    public void setKidsAreaTable(boolean kidsAreaSeat) {
        this.kidsAreaTable = kidsAreaSeat;
    }
}