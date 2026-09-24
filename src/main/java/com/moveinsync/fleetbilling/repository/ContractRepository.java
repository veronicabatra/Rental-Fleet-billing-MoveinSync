package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.Contract;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("""
        SELECT c FROM Contract c
        WHERE c.vehicle.id = :vehicleId
        AND c.validFrom <= :date
        AND c.validTo >= :date
    """)
    List<Contract> findActiveContracts(
            @Param("vehicleId") Long vehicleId,
            @Param("date") LocalDate date
    );
}