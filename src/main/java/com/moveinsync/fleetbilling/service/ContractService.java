package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.dto.ContractRequest;
import com.moveinsync.fleetbilling.entity.Contract;
import com.moveinsync.fleetbilling.entity.ContractSlab;
import com.moveinsync.fleetbilling.repository.ContractRepository;
import com.moveinsync.fleetbilling.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final VehicleRepository vehicleRepository;

    public ContractService(
            ContractRepository contractRepository,
            VehicleRepository vehicleRepository
    ) {
        this.contractRepository = contractRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Contract create(ContractRequest request) {

        VehicleCheck(request);

        var vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        String model = request.getChargingModel().toUpperCase();
        String pricing = request.getPricingType() == null
                ? "FLAT"
                : request.getPricingType().toUpperCase();

        if (!List.of("PER_KM", "PER_TRIP", "FIXED_MONTHLY").contains(model)) {
            throw new RuntimeException("Invalid charging model");
        }

        Contract contract = new Contract();

        contract.setVehicle(vehicle);
        contract.setChargingModel(model);
        contract.setPricingType(pricing);
        contract.setMonthlyFee(request.getMonthlyFee());
        contract.setIncludedKm(request.getIncludedKm());
        contract.setOverageRate(request.getOverageRate());
        contract.setPerKmRate(request.getPerKmRate());
        contract.setPerTripRate(request.getPerTripRate());
        contract.setNightChargePerTrip(request.getNightChargePerTrip());
        contract.setWaitingChargePerHour(request.getWaitingChargePerHour());
        contract.setValidFrom(request.getValidFrom());
        contract.setValidTo(request.getValidTo());

        for (ContractRequest.SlabRequest slabRequest : request.getSlabs()) {

            ContractSlab slab = new ContractSlab();

            slab.setContract(contract);
            slab.setMinKm(slabRequest.getMinKm());
            slab.setMaxKm(slabRequest.getMaxKm());
            slab.setRatePerKm(slabRequest.getRatePerKm());

            contract.getSlabs().add(slab);
        }

        return contractRepository.save(contract);
    }

    private void VehicleCheck(ContractRequest request) {

        if (request.getChargingModel() == null ||
                request.getValidFrom() == null ||
                request.getValidTo() == null) {

            throw new RuntimeException(
                    "Charging model and contract validity dates are required"
            );
        }

        if (request.getValidTo().isBefore(request.getValidFrom())) {
            throw new RuntimeException("validTo cannot be before validFrom");
        }

        String model = request.getChargingModel().toUpperCase();

        if (model.equals("PER_KM") &&
                request.getPerKmRate() == null &&
                request.getSlabs().isEmpty()) {

            throw new RuntimeException("PER_KM requires perKmRate or slabs");
        }

        if (model.equals("PER_TRIP") &&
                request.getPerTripRate() == null) {

            throw new RuntimeException("PER_TRIP requires perTripRate");
        }

        if (model.equals("FIXED_MONTHLY") &&
                request.getMonthlyFee() == null) {

            throw new RuntimeException("FIXED_MONTHLY requires monthlyFee");
        }
    }

    public List<Contract> getAll() {
        return contractRepository.findAll();
    }

    public Contract getById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found: " + id));
    }
}