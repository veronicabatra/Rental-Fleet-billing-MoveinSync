package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
}