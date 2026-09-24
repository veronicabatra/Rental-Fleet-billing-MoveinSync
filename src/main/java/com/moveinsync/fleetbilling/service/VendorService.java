package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.dto.VendorRequest;
import com.moveinsync.fleetbilling.entity.Vendor;
import com.moveinsync.fleetbilling.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorService {

    private final VendorRepository repository;

    public VendorService(VendorRepository repository) {
        this.repository = repository;
    }

    public Vendor create(VendorRequest request) {
        return repository.save(new Vendor(request.getName()));
    }

    public List<Vendor> getAll() {
        return repository.findAll();
    }

    public Vendor getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found: " + id));
    }
}