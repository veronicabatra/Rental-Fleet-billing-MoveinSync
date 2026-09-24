package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByVehicleIdAndTripDateBetween(
            Long vehicleId,
            LocalDate start,
            LocalDate end
    );
}