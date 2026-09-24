package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.entity.Trip;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class fraudDetectionService {

    public List<String> check(Trip trip) {

        List<String> reasons = new ArrayList<>();

        if (trip.getRideRecordId() == null ||
                trip.getRideRecordId().isBlank()) {

            reasons.add("MISSING_RIDE_RECORD");
        }

        if (trip.getDistanceKm() == null ||
                trip.getDistanceKm().compareTo(BigDecimal.ZERO) <= 0) {

            reasons.add("INVALID_DISTANCE");
        }

        if (trip.getDistanceKm() != null &&
                trip.getDistanceKm().compareTo(new BigDecimal("2000")) > 0) {

            reasons.add("IMPOSSIBLE_DISTANCE");
        }

        return reasons;
    }
}