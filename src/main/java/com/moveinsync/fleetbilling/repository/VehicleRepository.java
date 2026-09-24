package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}