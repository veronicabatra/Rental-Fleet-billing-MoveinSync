package com.moveinsync.fleetbilling.repository;

import com.moveinsync.fleetbilling.entity.ContractSlab;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContractSlabRepository extends JpaRepository<ContractSlab, Long> {

    List<ContractSlab> findByContractIdOrderByMinKmAsc(Long contractId);
}