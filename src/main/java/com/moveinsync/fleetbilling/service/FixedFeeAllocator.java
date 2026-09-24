package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.entity.Trip;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
public class FixedFeeAllocator {

    public Map<Long, BigDecimal> allocate(
            BigDecimal totalAmount,
            List<Trip> trips
    ) {

        Map<Long, BigDecimal> result = new LinkedHashMap<>();

        if (trips.isEmpty()) {
            return result;
        }

        BigDecimal totalWeight = trips.stream()
                .map(this::weight)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            totalWeight = BigDecimal.valueOf(trips.size());

            for (Trip trip : trips) {
                result.put(
                        trip.getId(),
                        totalAmount.divide(
                                totalWeight,
                                2,
                                RoundingMode.DOWN
                        )
                );
            }

            distributeRemainder(totalAmount, result);
            return result;
        }

        Map<Long, BigDecimal> remainders = new HashMap<>();
        BigDecimal allocated = BigDecimal.ZERO;

        for (Trip trip : trips) {

            BigDecimal raw = totalAmount
                    .multiply(weight(trip))
                    .divide(totalWeight, 10, RoundingMode.HALF_UP);

            BigDecimal rounded = raw.setScale(2, RoundingMode.DOWN);

            result.put(trip.getId(), rounded);

            allocated = allocated.add(rounded);

            remainders.put(
                    trip.getId(),
                    raw.subtract(rounded)
            );
        }

        BigDecimal remainder = totalAmount.subtract(allocated);

        int cents = remainder
                .movePointRight(2)
                .intValue();

        List<Long> orderedIds = trips.stream()
        .map(Trip::getId)
        .sorted((id1, id2) -> {

            int remainderComparison =
                    remainders.get(id2).compareTo(remainders.get(id1));

            if (remainderComparison != 0) {
                return remainderComparison;
            }

            return Long.compare(id1, id2);
        })
        .toList();

        for (int i = 0; i < cents; i++) {

            Long id = orderedIds.get(i % orderedIds.size());

            result.put(
                    id,
                    result.get(id).add(new BigDecimal("0.01"))
            );
        }

        return result;
    }

    private BigDecimal weight(Trip trip) {
        return trip.getDistanceKm()
                .add(trip.getDeadKm());
    }

    private void distributeRemainder(
            BigDecimal totalAmount,
            Map<Long, BigDecimal> result
    ) {

        BigDecimal sum = result.values()
                .stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainder = totalAmount.subtract(sum);

        int cents = remainder
                .movePointRight(2)
                .intValue();

        List<Long> ids = new ArrayList<>(result.keySet());
        Collections.sort(ids);

        for (int i = 0; i < cents; i++) {

            Long id = ids.get(i % ids.size());

            result.put(
                    id,
                    result.get(id).add(new BigDecimal("0.01"))
            );
        }
    }
}