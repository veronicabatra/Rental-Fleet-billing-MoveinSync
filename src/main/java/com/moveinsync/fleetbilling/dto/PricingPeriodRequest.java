package com.moveinsync.fleetbilling.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PricingPeriodRequest {

    private LocalDate fromDate;
    private LocalDate toDate;

    private BigDecimal perKmRate;
    private BigDecimal perTripRate;

    private BigDecimal monthlyFee;
    private BigDecimal includedKm;
    private BigDecimal overageRate;

    private List<SlabRequest> slabs;

    public PricingPeriodRequest() {
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
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

    public List<SlabRequest> getSlabs() {
        return slabs;
    }

    public void setSlabs(List<SlabRequest> slabs) {
        this.slabs = slabs;
    }
}