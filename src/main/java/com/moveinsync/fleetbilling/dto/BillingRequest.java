package com.moveinsync.fleetbilling.dto;

import java.math.BigDecimal;
import java.util.List;

public class BillingRequest {

    private Long vehicleId;
    private String month;

    private String chargingModel;
    private String pricingType;

    private BigDecimal perKmRate;
    private BigDecimal perTripRate;

    private BigDecimal monthlyFee;
    private BigDecimal includedKm;
    private BigDecimal overageRate;

    private BigDecimal nightChargePerTrip;
    private BigDecimal waitingChargePerHour;

    private String tollMode;

    private List<SlabRequest> slabs;

    // =====================================================
    // MID-MONTH RATE CHANGE
    // =====================================================

    private boolean rateChange;

    private List<PricingPeriodRequest> pricingPeriods;

    // =====================================================
    // TRIPS ENTERED FROM UI
    // =====================================================

    private List<TripRequest> trips;

    public BillingRequest() {
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getChargingModel() {
        return chargingModel;
    }

    public void setChargingModel(String chargingModel) {
        this.chargingModel = chargingModel;
    }

    public String getPricingType() {
        return pricingType;
    }

    public void setPricingType(String pricingType) {
        this.pricingType = pricingType;
    }

    public BigDecimal getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(BigDecimal perKmRate) {
        this.perKmRate = perKmRate;
    }

    public BigDecimal getPerTripRate() {
        return perTripRate;
    }

    public void setPerTripRate(BigDecimal perTripRate) {
        this.perTripRate = perTripRate;
    }

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getIncludedKm() {
        return includedKm;
    }

    public void setIncludedKm(BigDecimal includedKm) {
        this.includedKm = includedKm;
    }

    public BigDecimal getOverageRate() {
        return overageRate;
    }

    public void setOverageRate(BigDecimal overageRate) {
        this.overageRate = overageRate;
    }

    public BigDecimal getNightChargePerTrip() {
        return nightChargePerTrip;
    }

    public void setNightChargePerTrip(BigDecimal nightChargePerTrip) {
        this.nightChargePerTrip = nightChargePerTrip;
    }

    public BigDecimal getWaitingChargePerHour() {
        return waitingChargePerHour;
    }

    public void setWaitingChargePerHour(BigDecimal waitingChargePerHour) {
        this.waitingChargePerHour = waitingChargePerHour;
    }

    public String getTollMode() {
        return tollMode;
    }

    public void setTollMode(String tollMode) {
        this.tollMode = tollMode;
    }

    public List<SlabRequest> getSlabs() {
        return slabs;
    }

    public void setSlabs(List<SlabRequest> slabs) {
        this.slabs = slabs;
    }

    // =====================================================
    // MID-MONTH RATE CHANGE GETTERS / SETTERS
    // =====================================================

    public boolean isRateChange() {
        return rateChange;
    }

    public void setRateChange(boolean rateChange) {
        this.rateChange = rateChange;
    }

    public List<PricingPeriodRequest> getPricingPeriods() {
        return pricingPeriods;
    }

    public void setPricingPeriods(
            List<PricingPeriodRequest> pricingPeriods
    ) {
        this.pricingPeriods = pricingPeriods;
    }

    // =====================================================
    // TRIPS GETTERS / SETTERS
    // =====================================================

    public List<TripRequest> getTrips() {
        return trips;
    }

    public void setTrips(List<TripRequest> trips) {
        this.trips = trips;
    }
}