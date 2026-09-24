package com.moveinsync.fleetbilling.dto;

import java.math.BigDecimal;

public class SlabRequest {

    private BigDecimal minKm;
    private BigDecimal maxKm;
    private BigDecimal ratePerKm;


    public SlabRequest() {
    }


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