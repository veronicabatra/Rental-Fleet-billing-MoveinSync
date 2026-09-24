package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.TripBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripBillRepository extends JpaRepository<TripBill, Long> {

    boolean existsByTripId(Long tripId);
}