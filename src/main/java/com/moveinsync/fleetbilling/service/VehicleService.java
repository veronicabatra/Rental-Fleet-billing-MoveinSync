package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.dto.VehicleRequest;
import com.moveinsync.fleetbilling.entity.Vehicle;
import com.moveinsync.fleetbilling.repository.VehicleRepository;
import com.moveinsync.fleetbilling.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VendorRepository vendorRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            VendorRepository vendorRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.vendorRepository = vendorRepository;
    }

    public Vehicle create(VehicleRequest request) {

        var vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() ->
                        new RuntimeException("Vendor not found: " + request.getVendorId()));

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setModel(request.getModel());
        vehicle.setVendor(vendor);

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle getById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + id));
    }
}