package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.entity.Trip;
import com.moveinsync.fleetbilling.service.TripService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService service;

    public TripController(TripService service) {
        this.service = service;
    }

    @GetMapping
    public List<Trip> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Trip getById(@PathVariable Long id) {
        return service.getById(id);
    }
}