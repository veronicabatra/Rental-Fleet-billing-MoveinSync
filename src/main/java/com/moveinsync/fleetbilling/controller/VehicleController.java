package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.dto.VehicleRequest;
import com.moveinsync.fleetbilling.entity.Vehicle;
import com.moveinsync.fleetbilling.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle create(@Valid @RequestBody VehicleRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<Vehicle> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable Long id) {
        return service.getById(id);
    }
}