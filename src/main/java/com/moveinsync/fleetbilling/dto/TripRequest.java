package com.moveinsync.fleetbilling.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TripRequest {

    @NotNull
    private LocalDate tripDate;

    @NotNull
    private BigDecimal distanceKm;

    private BigDecimal deadKm = BigDecimal.ZERO;

    private boolean nightTrip;

    private BigDecimal waitingHours = BigDecimal.ZERO;

    private BigDecimal tollAmount = BigDecimal.ZERO;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public TripRequest() {
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public LocalDate getTripDate() {
        return tripDate;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public BigDecimal getDeadKm() {
        return deadKm;
    }

    public boolean isNightTrip() {
        return nightTrip;
    }

    public BigDecimal getWaitingHours() {
        return waitingHours;
    }

    public BigDecimal getTollAmount() {
        return tollAmount;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public void setDeadKm(BigDecimal deadKm) {
        this.deadKm = deadKm;
    }

    public void setNightTrip(boolean nightTrip) {
        this.nightTrip = nightTrip;
    }

    public void setWaitingHours(BigDecimal waitingHours) {
        this.waitingHours = waitingHours;
    }

    public void setTollAmount(BigDecimal tollAmount) {
        this.tollAmount = tollAmount;
    }
}