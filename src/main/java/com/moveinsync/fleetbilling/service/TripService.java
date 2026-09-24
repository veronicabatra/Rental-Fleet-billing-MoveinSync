package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.dto.TripRequest;
import com.moveinsync.fleetbilling.entity.Trip;
import com.moveinsync.fleetbilling.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getAll() {
        return tripRepository.findAll();
    }

    public Trip getById(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + id));
    }
}