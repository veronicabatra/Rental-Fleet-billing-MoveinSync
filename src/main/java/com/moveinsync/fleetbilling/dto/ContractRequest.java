package com.moveinsync.fleetbilling.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContractRequest {

    @NotNull
    private Long vehicleId;

    private String chargingModel;
    private String pricingType;

    private BigDecimal monthlyFee;
    private BigDecimal includedKm;
    private BigDecimal overageRate;
    private BigDecimal perKmRate;
    private BigDecimal perTripRate;
    private BigDecimal nightChargePerTrip;
    private BigDecimal waitingChargePerHour;

    private LocalDate validFrom;
    private LocalDate validTo;

    private List<SlabRequest> slabs = new ArrayList<>();

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    public List<SlabRequest> getSlabs() {
        return slabs;
    }

    public void setSlabs(List<SlabRequest> slabs) {
        this.slabs = slabs;
    }

    public static class SlabRequest {

        private BigDecimal minKm;
        private BigDecimal maxKm;
        private BigDecimal ratePerKm;

        public BigDecimal getMinKm() {
            return minKm;
        }

        public void setMinKm(BigDecimal minKm) {
            this.minKm = minKm;
        }

        public BigDecimal getMaxKm() {
            return maxKm;
        }

        public void setMaxKm(BigDecimal maxKm) {
            this.maxKm = maxKm;
        }

        public BigDecimal getRatePerKm() {
            return ratePerKm;
        }

        public void setRatePerKm(BigDecimal ratePerKm) {
            this.ratePerKm = ratePerKm;
        }
    }
}