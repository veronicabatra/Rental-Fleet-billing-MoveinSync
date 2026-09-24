package com.moveinsync.fleetbilling.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private LocalDate tripDate;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal distanceKm;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal deadKm = BigDecimal.ZERO;

    private boolean nightTrip;

    @Column(precision = 19, scale = 3)
    private BigDecimal waitingHours = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal tollAmount = BigDecimal.ZERO;

    private String rideRecordId;

    public Trip() {
    }

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public LocalDate getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDate tripDate) {
        this.tripDate = tripDate;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getDeadKm() {
        return deadKm;
    }

    public void setDeadKm(BigDecimal deadKm) {
        this.deadKm = deadKm;
    }

    public boolean isNightTrip() {
        return nightTrip;
    }

    public void setNightTrip(boolean nightTrip) {
        this.nightTrip = nightTrip;
    }

    public BigDecimal getWaitingHours() {
        return waitingHours;
    }

    public void setWaitingHours(BigDecimal waitingHours) {
        this.waitingHours = waitingHours;
    }

    public BigDecimal getTollAmount() {
        return tollAmount;
    }

    public void setTollAmount(BigDecimal tollAmount) {
        this.tollAmount = tollAmount;
    }

    public String getRideRecordId() {
        return rideRecordId;
    }

    public void setRideRecordId(String rideRecordId) {
        this.rideRecordId = rideRecordId;
    }
}