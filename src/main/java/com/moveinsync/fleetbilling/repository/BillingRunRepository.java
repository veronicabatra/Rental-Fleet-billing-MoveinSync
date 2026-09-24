package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.BillingRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface BillingRunRepository extends JpaRepository<BillingRun, Long> {

    Optional<BillingRun> findByVehicleIdAndBillingMonth(
            Long vehicleId,
            LocalDate billingMonth
    );
}