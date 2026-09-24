package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.dto.VendorRequest;
import com.moveinsync.fleetbilling.entity.Vendor;
import com.moveinsync.fleetbilling.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService service;

    public VendorController(VendorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vendor create(@Valid @RequestBody VendorRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Vendor> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Vendor getById(@PathVariable Long id) {
        return service.getById(id);
    }
}