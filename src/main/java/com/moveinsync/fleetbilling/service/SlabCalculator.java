package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.entity.ContractSlab;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

@Component
public class SlabCalculator {

    public BigDecimal calculate(
            BigDecimal distance,
            List<ContractSlab> slabs
    ) {

        if (distance == null
                || distance.compareTo(BigDecimal.ZERO) <= 0) {

            return BigDecimal.ZERO;
        }

        if (slabs == null || slabs.isEmpty()) {

            throw new RuntimeException(
                    "No pricing slabs configured"
            );
        }

        List<ContractSlab> sortedSlabs =
                slabs.stream()
                        .sorted(
                                Comparator.comparing(
                                        ContractSlab::getMinKm
                                )
                        )
                        .toList();

        BigDecimal total =
                BigDecimal.ZERO;

        for (ContractSlab slab : sortedSlabs) {

            BigDecimal minKm =
                    slab.getMinKm();

            BigDecimal maxKm =
                    slab.getMaxKm();

            BigDecimal rate =
                    slab.getRatePerKm();

            if (minKm == null) {

                throw new RuntimeException(
                        "Slab minimum KM cannot be null"
                );
            }

            if (rate == null
                    || rate.compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Invalid slab rate"
                );
            }

            /*
             * If the requested distance has not
             * reached this slab, skip it.
             */
            if (distance.compareTo(minKm) <= 0) {
                continue;
            }

            BigDecimal slabEnd;

            if (maxKm == null) {

                slabEnd = distance;

            } else {

                slabEnd =
                        distance.min(maxKm);
            }

            BigDecimal slabDistance =
                    slabEnd.subtract(minKm);

            if (slabDistance.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                BigDecimal amount =
                        slabDistance
                                .multiply(rate)
                                .setScale(
                                        2,
                                        RoundingMode.HALF_UP
                                );

                total =
                        total.add(amount);
            }
        }

        return total.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}