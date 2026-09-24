package com.moveinsync.fleetbilling.service;

import com.moveinsync.fleetbilling.dto.BillingRequest;
import com.moveinsync.fleetbilling.dto.BillingResponse;
import com.moveinsync.fleetbilling.dto.PricingPeriodRequest;
import com.moveinsync.fleetbilling.dto.SlabRequest;
import com.moveinsync.fleetbilling.dto.TripRequest;
import com.moveinsync.fleetbilling.entity.BillingRun;
import com.moveinsync.fleetbilling.entity.Contract;
import com.moveinsync.fleetbilling.entity.ContractSlab;
import com.moveinsync.fleetbilling.entity.Trip;
import com.moveinsync.fleetbilling.entity.TripBill;
import com.moveinsync.fleetbilling.entity.Vehicle;
import com.moveinsync.fleetbilling.repository.BillingRunRepository;
import com.moveinsync.fleetbilling.repository.TripBillRepository;
import com.moveinsync.fleetbilling.repository.TripRepository;
import com.moveinsync.fleetbilling.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BillingService {

    private final VehicleRepository vehicleRepository;
    private final TripRepository tripRepository;
    private final BillingRunRepository billingRunRepository;
    private final TripBillRepository tripBillRepository;
    private final SlabCalculator slabCalculator;
    private final FixedFeeAllocator fixedFeeAllocator;
    private final fraudDetectionService fraudDetectionService;

    public BillingService(
            VehicleRepository vehicleRepository,
            TripRepository tripRepository,
            BillingRunRepository billingRunRepository,
            TripBillRepository tripBillRepository,
            SlabCalculator slabCalculator,
            FixedFeeAllocator fixedFeeAllocator,
            fraudDetectionService fraudDetectionService
    ) {
        this.vehicleRepository = vehicleRepository;
        this.tripRepository = tripRepository;
        this.billingRunRepository = billingRunRepository;
        this.tripBillRepository = tripBillRepository;
        this.slabCalculator = slabCalculator;
        this.fixedFeeAllocator = fixedFeeAllocator;
        this.fraudDetectionService = fraudDetectionService;
    }

    @Transactional
    public BillingResponse runBilling(BillingRequest request) {

        if (request == null) {
            throw new RuntimeException("Billing request cannot be null");
        }

        Long vehicleId = request.getVehicleId();
        String month = request.getMonth();

        if (vehicleId == null) {
            throw new RuntimeException("Vehicle ID is required");
        }

        if (month == null || month.isBlank()) {
            throw new RuntimeException(
                    "Billing month is required. Use YYYY-MM"
            );
        }

        YearMonth yearMonth;

        try {
            yearMonth = YearMonth.parse(month);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Invalid month format. Use YYYY-MM"
            );
        }

        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();

        validateBillingRequest(request);

        validatePricingPeriods(
                request,
                monthStart,
                monthEnd
        );

        /*
         * Idempotency:
         * Same vehicle + same month should not
         * create another billing run.
         */
        var existing =
                billingRunRepository.findByVehicleIdAndBillingMonth(
                        vehicleId,
                        monthStart
                );

        if (existing.isPresent()) {
            return buildResponse(existing.get());
        }

        Vehicle vehicle =
                vehicleRepository.findById(vehicleId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Vehicle not found: " + vehicleId
                                )
                        );

        /*
         * Trips are entered from the UI.
         * We do NOT fetch old trips from DB
         * for this billing request.
         */
        List<TripRequest> tripRequests =
                request.getTrips();

        if (tripRequests == null
                || tripRequests.isEmpty()) {

            throw new RuntimeException(
                    "At least one trip is required"
            );
        }

        List<Trip> trips =
                new ArrayList<>();

        /*
         * Convert UI trip requests into Trip entities
         * and save them into MySQL.
         */
        for (TripRequest tripRequest :
                tripRequests) {

            if (tripRequest == null) {
                throw new RuntimeException(
                        "Invalid trip data"
                );
            }

            if (tripRequest.getTripDate() == null) {
                throw new RuntimeException(
                        "Trip date is required"
                );
            }

            if (tripRequest.getTripDate().isBefore(monthStart)
                    || tripRequest.getTripDate().isAfter(monthEnd)) {

                throw new RuntimeException(
                        "Trip date "
                                + tripRequest.getTripDate()
                                + " does not belong to billing month "
                                + month
                );
            }

            if (tripRequest.getDistanceKm() == null) {
                throw new RuntimeException(
                        "Trip distance is required"
                );
            }

            if (tripRequest.getDistanceKm()
                    .compareTo(BigDecimal.ZERO) <= 0) {

                throw new RuntimeException(
                        "Trip distance must be greater than zero"
                );
            }

            Trip trip =
                    new Trip();

            trip.setVehicle(vehicle);

            trip.setTripDate(
                    tripRequest.getTripDate()
            );

            trip.setDistanceKm(
                    safe(
                            tripRequest.getDistanceKm()
                    )
            );

            trip.setDeadKm(
                    safe(
                            tripRequest.getDeadKm()
                    )
            );

            trip.setNightTrip(
                    tripRequest.isNightTrip()
            );

            trip.setWaitingHours(
                    safe(
                            tripRequest.getWaitingHours()
                    )
            );

            trip.setTollAmount(
                    safe(
                            tripRequest.getTollAmount()
                    )
            );

            /*
             * rideRecordId is intentionally not taken
             * from the UI.
             */

            Trip savedTrip =
                    tripRepository.save(trip);

            trips.add(savedTrip);
        }

        if (trips.isEmpty()) {
            throw new RuntimeException(
                    "No trips were provided"
            );
        }

        BillingRun run =
                new BillingRun();

        run.setVehicle(vehicle);

        run.setBillingMonth(
                monthStart
        );

        run.setStatus(
                "PROCESSING"
        );

        run.setTotalAmount(
                BigDecimal.ZERO
        );

        billingRunRepository.save(run);

        Contract selectedContract =
                buildContractFromRequest(
                        request
                );

        /*
         * Fraud checking.
         */
        List<Trip> validTrips =
                new ArrayList<>();

        Map<Long, List<String>> fraudMap =
                new HashMap<>();

        for (Trip trip : trips) {

            List<String> reasons =
                    fraudDetectionService.check(
                            trip
                    );

            if (!reasons.isEmpty()) {

                fraudMap.put(
                        trip.getId(),
                        reasons
                );
            }

            /*
             * Only actual billing-blocking fraud
             * removes the trip from normal billing.
             *
             * Missing ride record remains a flag,
             * but does not stop calculation.
             */
            boolean blockingFraud =
                    reasons.stream()
                            .anyMatch(
                                    reason ->
                                            reason.equals(
                                                    "INVALID_DISTANCE"
                                            )
                                                    || reason.equals(
                                                    "IMPOSSIBLE_DISTANCE"
                                            )
                            );

            if (!blockingFraud) {
                validTrips.add(trip);
            }
        }

        /*
         * Current request contains one base contract.
         *
         * Mid-month rate changes are resolved inside
         * calculateBaseCharge() using trip date.
         */
        Map<Long, Contract> tripContracts =
                new HashMap<>();

        for (Trip trip : validTrips) {

            tripContracts.put(
                    trip.getId(),
                    selectedContract
            );
        }

        /*
         * Fixed monthly billing.
         */
        BigDecimal fixedPool =
                calculateFixedMonthlyPool(
                        validTrips,
                        selectedContract
                );

        Map<Long, BigDecimal> fixedAllocations =
                fixedFeeAllocator.allocate(
                        fixedPool.setScale(
                                2,
                                RoundingMode.HALF_UP
                        ),
                        validTrips
                );

        BigDecimal grandTotal =
                BigDecimal.ZERO;

        int flagged = 0;

        List<TripBill> generatedBills =
                new ArrayList<>();

        /*
         * Generate one bill for every trip.
         */
        for (Trip trip : trips) {

            TripBill bill =
                    new TripBill();

            bill.setBillingRun(run);

            bill.setTrip(trip);

            List<String> fraudReasons =
                    fraudMap.get(
                            trip.getId()
                    );

            boolean blockingFraud =
                    fraudReasons != null
                            && fraudReasons.stream()
                            .anyMatch(
                                    reason ->
                                            reason.equals(
                                                    "INVALID_DISTANCE"
                                            )
                                                    || reason.equals(
                                                    "IMPOSSIBLE_DISTANCE"
                                            )
                            );

            if (fraudReasons != null
                    && !fraudReasons.isEmpty()) {

                flagged++;

                bill.setFraudFlag(true);

                bill.setFraudReason(
                        String.join(
                                ", ",
                                fraudReasons
                        )
                );

            } else {

                bill.setFraudFlag(false);

                bill.setFraudReason(null);
            }

            /*
             * Blocking fraud:
             * all billing amounts become zero.
             */
            if (blockingFraud) {

                bill.setBaseCharge(
                        BigDecimal.ZERO
                );

                bill.setFixedAllocation(
                        BigDecimal.ZERO
                );

                bill.setNightCharge(
                        BigDecimal.ZERO
                );

                bill.setWaitingCharge(
                        BigDecimal.ZERO
                );

                bill.setTollCharge(
                        BigDecimal.ZERO
                );

                bill.setTotalAmount(
                        BigDecimal.ZERO
                );

                tripBillRepository.save(
                        bill
                );

                generatedBills.add(
                        bill
                );

                continue;
            }

            Contract contract =
                    tripContracts.get(
                            trip.getId()
                    );

            if (contract == null) {

                throw new RuntimeException(
                        "Billing configuration not found for trip: "
                                + trip.getId()
                );
            }

            /*
             * Base charge.
             *
             * Important:
             * If rateChange = true,
             * this method automatically selects
             * the correct period using trip date.
             */
            BigDecimal baseCharge =
                    calculateBaseCharge(
                            trip,
                            contract,
                            request
                    );

            BigDecimal fixedAllocation =
                    fixedAllocations.getOrDefault(
                            trip.getId(),
                            BigDecimal.ZERO
                    );

            BigDecimal nightCharge =
                    calculateNightCharge(
                            trip,
                            contract
                    );

            BigDecimal waitingCharge =
                    calculateWaitingCharge(
                            trip,
                            contract
                    );

            BigDecimal tollCharge =
                    calculateTollCharge(
                            trip,
                            request
                    );

            BigDecimal total =
                    baseCharge
                            .add(fixedAllocation)
                            .add(nightCharge)
                            .add(waitingCharge)
                            .add(tollCharge)
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            bill.setBaseCharge(
                    baseCharge
            );

            bill.setFixedAllocation(
                    fixedAllocation
            );

            bill.setNightCharge(
                    nightCharge
            );

            bill.setWaitingCharge(
                    waitingCharge
            );

            bill.setTollCharge(
                    tollCharge
            );

            bill.setTotalAmount(
                    total
            );

            tripBillRepository.save(
                    bill
            );

            generatedBills.add(
                    bill
            );

            grandTotal =
                    grandTotal.add(
                            total
                    );
        }

        grandTotal =
                grandTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                );

        run.setTotalAmount(
                grandTotal
        );

        run.setStatus(
                "COMPLETED"
        );

        billingRunRepository.save(
                run
        );

        return buildDetailedResponse(
                run,
                trips,
                generatedBills,
                fixedPool,
                flagged,
                selectedContract
        );
    }


    // =====================================================
    // VALIDATE BILLING REQUEST
    // =====================================================

    private void validateBillingRequest(
            BillingRequest request
    ) {

        String model =
                request.getChargingModel();

        if (model == null
                || model.isBlank()) {

            throw new RuntimeException(
                    "Charging model is required"
            );
        }

        model =
                model.toUpperCase();

        /*
         * PER KM
         */
        if (model.equals("PER_KM")) {

            String pricingType =
                    request.getPricingType();

            if (pricingType == null
                    || pricingType.isBlank()) {

                throw new RuntimeException(
                        "Pricing type is required for PER_KM"
                );
            }

            if (pricingType.equalsIgnoreCase(
                    "FLAT"
            )) {

                /*
                 * Normal mode:
                 * perKmRate required.
                 *
                 * Rate-change mode:
                 * individual periods contain
                 * their own rates.
                 */
                if (!request.isRateChange()
                        && safe(
                        request.getPerKmRate()
                ).compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

                    throw new RuntimeException(
                            "Per KM rate must be greater than zero"
                    );
                }

            } else if (
                    pricingType.equalsIgnoreCase(
                            "TIERED"
                    )
            ) {

                /*
                 * Normal mode:
                 * slabs are required.
                 *
                 * Rate-change mode:
                 * every period has its own slabs.
                 */
                if (!request.isRateChange()) {

                    List<SlabRequest> slabs =
                            request.getSlabs();

                    if (slabs == null
                            || slabs.isEmpty()) {

                        throw new RuntimeException(
                                "At least one slab is required for TIERED pricing"
                        );
                    }

                    validateSlabs(
                            slabs
                    );
                }

            } else {

                throw new RuntimeException(
                        "Unsupported pricing type: "
                                + pricingType
                );
            }


        /*
         * PER TRIP
         */
        } else if (
                model.equals("PER_TRIP")
        ) {

            if (!request.isRateChange()
                    && safe(
                    request.getPerTripRate()
            ).compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new RuntimeException(
                        "Per Trip rate must be greater than zero"
                );
            }


        /*
         * FIXED MONTHLY
         */
        } else if (
                model.equals("FIXED_MONTHLY")
        ) {

            /*
             * Mid-month fixed monthly pricing
             * is intentionally not supported here.
             */
            if (request.isRateChange()) {

                throw new RuntimeException(
                        "Mid-month rate change for FIXED_MONTHLY is not supported yet"
                );
            }

            if (safe(
                    request.getMonthlyFee()
            ).compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new RuntimeException(
                        "Monthly fee must be greater than zero"
                );
            }

            if (safe(
                    request.getIncludedKm()
            ).compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new RuntimeException(
                        "Included KM cannot be negative"
                );
            }

            if (safe(
                    request.getOverageRate()
            ).compareTo(
                    BigDecimal.ZERO
            ) < 0) {

                throw new RuntimeException(
                        "Overage rate cannot be negative"
                );
            }


        } else {

            throw new RuntimeException(
                    "Unsupported charging model: "
                            + model
            );
        }


        /*
         * Extra charges.
         */
        if (safe(
                request.getNightChargePerTrip()
        ).compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new RuntimeException(
                    "Night charge cannot be negative"
            );
        }

        if (safe(
                request.getWaitingChargePerHour()
        ).compareTo(
                BigDecimal.ZERO
        ) < 0) {

            throw new RuntimeException(
                    "Waiting charge cannot be negative"
            );
        }
    }


    // =====================================================
    // VALIDATE NORMAL SLABS
    // =====================================================

    private void validateSlabs(
            List<SlabRequest> slabs
    ) {

        for (SlabRequest slab :
                slabs) {

            if (slab == null) {

                throw new RuntimeException(
                        "Invalid slab configuration"
                );
            }

            if (safe(
                    slab.getRatePerKm()
            ).compareTo(
                    BigDecimal.ZERO
            ) <= 0) {

                throw new RuntimeException(
                        "Slab rate must be greater than zero"
                );
            }

            if (slab.getMinKm() == null) {

                throw new RuntimeException(
                        "Slab minimum KM is required"
                );
            }

            if (slab.getMaxKm() != null
                    && slab.getMaxKm()
                    .compareTo(
                            slab.getMinKm()
                    ) < 0) {

                throw new RuntimeException(
                        "Slab max KM cannot be less than min KM"
                );
            }
        }
    }


    // =====================================================
    // VALIDATE MID-MONTH PERIODS
    // =====================================================

    private void validatePricingPeriods(
            BillingRequest request,
            LocalDate monthStart,
            LocalDate monthEnd
    ) {

        /*
         * Normal billing:
         * nothing to validate.
         */
        if (!request.isRateChange()) {
            return;
        }

        /*
         * Fixed monthly is intentionally
         * not supported for rate changes.
         */
        if ("FIXED_MONTHLY".equalsIgnoreCase(
                request.getChargingModel()
        )) {

            throw new RuntimeException(
                    "Mid-month rate change for FIXED_MONTHLY is not supported yet"
            );
        }

        List<PricingPeriodRequest> periods =
                request.getPricingPeriods();

        if (periods == null
                || periods.isEmpty()) {

            throw new RuntimeException(
                    "Rate change is enabled but no pricing periods were provided"
            );
        }

        /*
         * Sort periods by start date.
         */
        periods.sort(
                Comparator.comparing(
                        PricingPeriodRequest::getFromDate
                )
        );

        /*
         * First period must start
         * on first day of billing month.
         */
        LocalDate expectedStart =
                monthStart;

        for (PricingPeriodRequest period :
                periods) {

            if (period == null
                    || period.getFromDate() == null
                    || period.getToDate() == null) {

                throw new RuntimeException(
                        "Pricing period dates are required"
                );
            }

            if (period.getFromDate()
                    .isAfter(
                            period.getToDate()
                    )) {

                throw new RuntimeException(
                        "Pricing period start date cannot be after end date"
                );
            }

            if (period.getFromDate()
                    .isBefore(monthStart)
                    || period.getToDate()
                    .isAfter(monthEnd)) {

                throw new RuntimeException(
                        "Pricing period must remain inside billing month"
                );
            }

            /*
             * No gaps.
             * No overlaps.
             */
            if (!period.getFromDate()
                    .equals(
                            expectedStart
                    )) {

                throw new RuntimeException(
                        "Pricing periods must continuously cover the billing month"
                );
            }


            /*
             * PER KM
             */
            if ("PER_KM".equalsIgnoreCase(
                    request.getChargingModel()
            )) {

                /*
                 * FLAT
                 */
                if ("FLAT".equalsIgnoreCase(
                        request.getPricingType()
                )) {

                    if (safe(
                            period.getPerKmRate()
                    ).compareTo(
                            BigDecimal.ZERO
                    ) <= 0) {

                        throw new RuntimeException(
                                "Per KM rate must be greater than zero for every period"
                        );
                    }


                /*
                 * TIERED
                 */
                } else if (
                        "TIERED".equalsIgnoreCase(
                                request.getPricingType()
                        )
                ) {

                    if (period.getSlabs() == null
                            || period.getSlabs()
                            .isEmpty()) {

                        throw new RuntimeException(
                                "Each pricing period needs at least one slab"
                        );
                    }

                    validateSlabs(
                            period.getSlabs()
                    );
                }
            }


            /*
             * PER TRIP
             */
            if ("PER_TRIP".equalsIgnoreCase(
                    request.getChargingModel()
            )) {

                if (safe(
                        period.getPerTripRate()
                ).compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

                    throw new RuntimeException(
                            "Per Trip rate must be greater than zero for every period"
                    );
                }
            }


            /*
             * Next period must begin
             * immediately after this period.
             */
            expectedStart =
                    period.getToDate()
                            .plusDays(1);
        }


        /*
         * Last period must end
         * on last day of month.
         */
        if (!expectedStart.equals(
                monthEnd.plusDays(1)
        )) {

            throw new RuntimeException(
                    "Pricing periods must cover the complete billing month"
            );
        }
    }


    // =====================================================
    // FIND PERIOD FOR TRIP DATE
    // =====================================================

    private PricingPeriodRequest findPricingPeriod(
            LocalDate tripDate,
            BillingRequest request
    ) {

        if (request.getPricingPeriods() == null
                || request.getPricingPeriods().isEmpty()) {

            throw new RuntimeException(
                    "Rate change is enabled but no pricing periods were provided"
            );
        }

        return request.getPricingPeriods()
                .stream()
                .filter(
                        period ->
                                period != null
                                        && period.getFromDate() != null
                                        && period.getToDate() != null

                                        && !tripDate.isBefore(
                                        period.getFromDate()
                                )

                                        && !tripDate.isAfter(
                                        period.getToDate()
                                )
                )
                .findFirst()
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "No pricing period found for trip date: "
                                                + tripDate
                                )
                );
    }


    // =====================================================
    // BUILD TEMPORARY CONTRACT
    // =====================================================

    private Contract buildContractFromRequest(
            BillingRequest request
    ) {

        Contract contract =
                new Contract();

        contract.setChargingModel(
                request.getChargingModel()
        );

        contract.setPricingType(
                request.getPricingType()
        );

        contract.setPerKmRate(
                request.getPerKmRate()
        );

        contract.setPerTripRate(
                request.getPerTripRate()
        );

        contract.setMonthlyFee(
                request.getMonthlyFee()
        );

        contract.setIncludedKm(
                request.getIncludedKm()
        );

        contract.setOverageRate(
                request.getOverageRate()
        );

        contract.setNightChargePerTrip(
                request.getNightChargePerTrip()
        );

        contract.setWaitingChargePerHour(
                request.getWaitingChargePerHour()
        );

        List<ContractSlab> slabs =
                new ArrayList<>();

        if (request.getSlabs() != null) {

            for (SlabRequest slabRequest :
                    request.getSlabs()) {

                ContractSlab slab =
                        new ContractSlab();

                slab.setContract(
                        contract
                );

                slab.setMinKm(
                        slabRequest.getMinKm()
                );

                slab.setMaxKm(
                        slabRequest.getMaxKm()
                );

                slab.setRatePerKm(
                        slabRequest.getRatePerKm()
                );

                slabs.add(
                        slab
                );
            }
        }

        contract.setSlabs(
                slabs
        );

        return contract;
    }


    // =====================================================
    // FIXED MONTHLY POOL
    // =====================================================

    private BigDecimal calculateFixedMonthlyPool(
            List<Trip> validTrips,
            Contract contract
    ) {

        if (!"FIXED_MONTHLY".equalsIgnoreCase(
                contract.getChargingModel()
        )) {

            return BigDecimal.ZERO;
        }

        BigDecimal fixedPool =
                safe(
                        contract.getMonthlyFee()
                );

        /*
         * Duty KM + dead running KM.
         */
        BigDecimal totalKm =
                validTrips.stream()
                        .map(
                                trip ->
                                        safe(
                                                trip.getDistanceKm()
                                        ).add(
                                                safe(
                                                        trip.getDeadKm()
                                                )
                                        )
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal includedKm =
                safe(
                        contract.getIncludedKm()
                );

        /*
         * Overage.
         */
        if (totalKm.compareTo(
                includedKm
        ) > 0) {

            BigDecimal overageKm =
                    totalKm.subtract(
                            includedKm
                    );

            BigDecimal overageAmount =
                    overageKm
                            .multiply(
                                    safe(
                                            contract.getOverageRate()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            fixedPool =
                    fixedPool.add(
                            overageAmount
                    );
        }

        return fixedPool.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // BASE CHARGE
    // =====================================================

    private BigDecimal calculateBaseCharge(
            Trip trip,
            Contract contract,
            BillingRequest request
    ) {

        String model =
                contract.getChargingModel()
                        .toUpperCase();


        /*
         * FIXED MONTHLY:
         * Monthly amount is handled by
         * FixedFeeAllocator.
         */
        if (model.equals(
                "FIXED_MONTHLY"
        )) {

            return BigDecimal.ZERO;
        }


        /*
         * PER TRIP
         */
        if (model.equals(
                "PER_TRIP"
        )) {

            /*
             * Mid-month rate.
             */
            if (request.isRateChange()) {

                PricingPeriodRequest period =
                        findPricingPeriod(
                                trip.getTripDate(),
                                request
                        );

                return safe(
                        period.getPerTripRate()
                ).setScale(
                        2,
                        RoundingMode.HALF_UP
                );
            }

            /*
             * Normal rate.
             */
            return safe(
                    contract.getPerTripRate()
            ).setScale(
                    2,
                    RoundingMode.HALF_UP
            );
        }


        /*
         * PER KM
         */
        if (model.equals(
                "PER_KM"
        )) {

            /*
             * Chargeable KM =
             * duty KM + dead KM.
             */
            BigDecimal km =
                    safe(
                            trip.getDistanceKm()
                    ).add(
                            safe(
                                    trip.getDeadKm()
                            )
                    );


            /*
             * MID-MONTH RATE CHANGE
             */
            if (request.isRateChange()) {

                PricingPeriodRequest period =
                        findPricingPeriod(
                                trip.getTripDate(),
                                request
                        );


                /*
                 * FLAT RATE
                 */
                if ("FLAT".equalsIgnoreCase(
                        request.getPricingType()
                )) {

                    return km
                            .multiply(
                                    safe(
                                            period.getPerKmRate()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );
                }


                /*
                 * TIERED RATE
                 */
                if ("TIERED".equalsIgnoreCase(
                        request.getPricingType()
                )) {

                    if (period.getSlabs() == null
                            || period.getSlabs().isEmpty()) {

                        throw new RuntimeException(
                                "Slabs are required for trip date: "
                                        + trip.getTripDate()
                        );
                    }

                    List<ContractSlab> periodSlabs =
                            new ArrayList<>();

                    for (SlabRequest slabRequest :
                            period.getSlabs()) {

                        ContractSlab slab =
                                new ContractSlab();

                        slab.setMinKm(
                                slabRequest.getMinKm()
                        );

                        slab.setMaxKm(
                                slabRequest.getMaxKm()
                        );

                        slab.setRatePerKm(
                                slabRequest.getRatePerKm()
                        );

                        periodSlabs.add(
                                slab
                        );
                    }

                    return slabCalculator.calculate(
                            km,
                            periodSlabs
                    );
                }
            }


            /*
             * NORMAL TIERED
             */
            if ("TIERED".equalsIgnoreCase(
                    contract.getPricingType()
            )) {

                return slabCalculator.calculate(
                        km,
                        contract.getSlabs()
                );
            }


            /*
             * NORMAL FLAT
             */
            return km
                    .multiply(
                            safe(
                                    contract.getPerKmRate()
                            )
                    )
                    .setScale(
                            2,
                            RoundingMode.HALF_UP
                    );
        }


        throw new RuntimeException(
                "Unsupported charging model: "
                        + model
        );
    }


    // =====================================================
    // NIGHT CHARGE
    // =====================================================

    private BigDecimal calculateNightCharge(
            Trip trip,
            Contract contract
    ) {

        if (!trip.isNightTrip()) {

            return BigDecimal.ZERO;
        }

        return safe(
                contract.getNightChargePerTrip()
        ).setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // WAITING CHARGE
    // =====================================================

    private BigDecimal calculateWaitingCharge(
            Trip trip,
            Contract contract
    ) {

        BigDecimal waitingHours =
                safe(
                        trip.getWaitingHours()
                );

        BigDecimal rate =
                safe(
                        contract.getWaitingChargePerHour()
                );

        return waitingHours
                .multiply(rate)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }


    // =====================================================
    // TOLL CHARGE
    // =====================================================

    private BigDecimal calculateTollCharge(
            Trip trip,
            BillingRequest request
    ) {

        if (request.getTollMode() != null
                && request.getTollMode()
                .equalsIgnoreCase(
                        "EXCLUDE"
                )) {

            return BigDecimal.ZERO;
        }

        return safe(
                trip.getTollAmount()
        ).setScale(
                2,
                RoundingMode.HALF_UP
        );
    }


    // =====================================================
    // DETAILED RESPONSE
    // =====================================================

    private BillingResponse buildDetailedResponse(
            BillingRun run,
            List<Trip> trips,
            List<TripBill> bills,
            BigDecimal fixedPool,
            int flagged,
            Contract selectedContract
    ) {

        BigDecimal totalDutyKm =
                BigDecimal.ZERO;

        BigDecimal totalDeadKm =
                BigDecimal.ZERO;

        BigDecimal baseCharges =
                BigDecimal.ZERO;

        BigDecimal fixedAllocationTotal =
                BigDecimal.ZERO;

        BigDecimal nightCharges =
                BigDecimal.ZERO;

        BigDecimal waitingCharges =
                BigDecimal.ZERO;

        BigDecimal tollCharges =
                BigDecimal.ZERO;


        /*
         * Total duty and dead KM.
         */
        for (Trip trip :
                trips) {

            totalDutyKm =
                    totalDutyKm.add(
                            safe(
                                    trip.getDistanceKm()
                            )
                    );

            totalDeadKm =
                    totalDeadKm.add(
                            safe(
                                    trip.getDeadKm()
                            )
                    );
        }


        /*
         * Charge totals.
         */
        for (TripBill bill :
                bills) {

            baseCharges =
                    baseCharges.add(
                            safe(
                                    bill.getBaseCharge()
                            )
                    );

            fixedAllocationTotal =
                    fixedAllocationTotal.add(
                            safe(
                                    bill.getFixedAllocation()
                            )
                    );

            nightCharges =
                    nightCharges.add(
                            safe(
                                    bill.getNightCharge()
                            )
                    );

            waitingCharges =
                    waitingCharges.add(
                            safe(
                                    bill.getWaitingCharge()
                            )
                    );

            tollCharges =
                    tollCharges.add(
                            safe(
                                    bill.getTollCharge()
                            )
                    );
        }


        totalDutyKm =
                totalDutyKm.setScale(
                        3,
                        RoundingMode.HALF_UP
                );

        totalDeadKm =
                totalDeadKm.setScale(
                        3,
                        RoundingMode.HALF_UP
                );


        BigDecimal totalChargeableKm =
                totalDutyKm
                        .add(totalDeadKm)
                        .setScale(
                                3,
                                RoundingMode.HALF_UP
                        );


        /*
         * Fixed fee reconciliation.
         */
        BigDecimal reconciliationDifference =
                fixedPool
                        .subtract(
                                fixedAllocationTotal
                        )
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        String reconciliationStatus =
                reconciliationDifference
                        .compareTo(
                                BigDecimal.ZERO
                        ) == 0
                        ? "RECONCILED"
                        : "NOT_RECONCILED";


        BillingResponse.ContractInfo contractInfo =
                new BillingResponse.ContractInfo(

                        selectedContract.getId(),

                        selectedContract.getChargingModel(),

                        selectedContract.getPricingType(),

                        selectedContract.getMonthlyFee(),

                        selectedContract.getIncludedKm(),

                        selectedContract.getOverageRate(),

                        selectedContract.getPerKmRate(),

                        selectedContract.getPerTripRate(),

                        selectedContract.getNightChargePerTrip(),

                        selectedContract.getWaitingChargePerHour(),

                        selectedContract.getValidFrom(),

                        selectedContract.getValidTo()
                );


        List<BillingResponse.TripBillInfo>
                tripBillInfoList =
                new ArrayList<>();


        for (TripBill bill :
                bills) {

            Trip trip =
                    bill.getTrip();

            BigDecimal dutyKm =
                    safe(
                            trip.getDistanceKm()
                    );

            BigDecimal deadKm =
                    safe(
                            trip.getDeadKm()
                    );

            BigDecimal totalKm =
                    dutyKm
                            .add(deadKm)
                            .setScale(
                                    3,
                                    RoundingMode.HALF_UP
                            );


            BillingResponse.TripBillInfo info =
                    new BillingResponse.TripBillInfo(

                            bill.getId(),

                            trip.getId(),

                            trip.getTripDate(),

                            dutyKm,

                            deadKm,

                            totalKm,

                            safe(
                                    bill.getBaseCharge()
                            ),

                            safe(
                                    bill.getFixedAllocation()
                            ),

                            safe(
                                    bill.getNightCharge()
                            ),

                            safe(
                                    bill.getWaitingCharge()
                            ),

                            safe(
                                    bill.getTollCharge()
                            ),

                            safe(
                                    bill.getTotalAmount()
                            ),

                            bill.isFraudFlag(),

                            bill.getFraudReason(),

                            selectedContract.getChargingModel(),

                            selectedContract.getPricingType()
                    );

            tripBillInfoList.add(
                    info
            );
        }


        return new BillingResponse(

                run.getId(),

                run.getVehicle().getId(),

                run.getBillingMonth()
                        .toString(),

                run.getTotalAmount(),

                trips.size(),

                flagged,

                totalDutyKm,

                totalDeadKm,

                totalChargeableKm,

                baseCharges.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                fixedAllocationTotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                nightCharges.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                waitingCharges.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                tollCharges.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                fixedPool.setScale(
                        2,
                        RoundingMode.HALF_UP
                ),

                reconciliationDifference,

                reconciliationStatus,

                run.getStatus(),

                contractInfo,

                tripBillInfoList
        );
    }


    // =====================================================
    // EXISTING BILLING RESPONSE
    // =====================================================

    private BillingResponse buildResponse(
            BillingRun run
    ) {

        List<TripBill> bills =
                new ArrayList<>(
                        run.getTripBills()
                );

        List<Trip> trips =
                new ArrayList<>();

        for (TripBill bill :
                bills) {

            if (bill.getTrip() != null) {

                trips.add(
                        bill.getTrip()
                );
            }
        }

        int flagged =
                (int) bills.stream()
                        .filter(
                                TripBill::isFraudFlag
                        )
                        .count();

        BigDecimal fixedPool =
                bills.stream()
                        .map(
                                TripBill::getFixedAllocation
                        )
                        .map(
                                this::safe
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Contract displayContract =
                createDisplayContractFromBills(
                        bills
                );

        return buildDetailedResponse(
                run,
                trips,
                bills,
                fixedPool,
                flagged,
                displayContract
        );
    }


    // =====================================================
    // DISPLAY CONTRACT
    // =====================================================

    private Contract createDisplayContractFromBills(
            List<TripBill> bills
    ) {

        Contract contract =
                new Contract();

        if (bills == null
                || bills.isEmpty()) {

            contract.setChargingModel(
                    "UNKNOWN"
            );

            contract.setPricingType(
                    "UNKNOWN"
            );

            return contract;
        }

        contract.setChargingModel(
                "GENERATED"
        );

        contract.setPricingType(
                "GENERATED"
        );

        return contract;
    }


    // =====================================================
    // SAFE BIG DECIMAL
    // =====================================================

    private BigDecimal safe(
            BigDecimal value
    ) {

        return value == null
                ? BigDecimal.ZERO
                : value;
    }
}