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

    private boolean cornerSeat;
    private boolean windowSeat;
    private boolean kidsAreaSeat;

    public RestaurantTable() {}

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

    public boolean isCornerSeat() {
        return cornerSeat;
    }

    public void setCornerSeat(boolean cornerSeat) {
        this.cornerSeat = cornerSeat;
    }

    public boolean isWindowSeat() {
        return windowSeat;
    }

    public void setWindowSeat(boolean windowSeat) {
        this.windowSeat = windowSeat;
    }

    public boolean isKidsAreaSeat() {
        return kidsAreaSeat;
    }

    public void setKidsAreaSeat(boolean kidsAreaSeat) {
        this.kidsAreaSeat = kidsAreaSeat;
    }
}