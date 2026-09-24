package com.moveinsync.fleetbilling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "trip_bills")
public class TripBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "billing_run_id", nullable = false)
    private BillingRun billingRun;

    @OneToOne
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    @Column(precision = 19, scale = 2)
    private BigDecimal baseCharge = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal fixedAllocation = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal nightCharge = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal waitingCharge = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal tollCharge = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private boolean fraudFlag;

    private String fraudReason;

    public TripBill() {
    }

    public Long getId() {
        return id;
    }

    public BillingRun getBillingRun() {
        return billingRun;
    }

    public void setBillingRun(BillingRun billingRun) {
        this.billingRun = billingRun;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public BigDecimal getBaseCharge() {
        return baseCharge;
    }

    public void setBaseCharge(BigDecimal baseCharge) {
        this.baseCharge = baseCharge;
    }

    public BigDecimal getFixedAllocation() {
        return fixedAllocation;
    }

    public void setFixedAllocation(BigDecimal fixedAllocation) {
        this.fixedAllocation = fixedAllocation;
    }

    public BigDecimal getNightCharge() {
        return nightCharge;
    }

    public void setNightCharge(BigDecimal nightCharge) {
        this.nightCharge = nightCharge;
    }

    public BigDecimal getWaitingCharge() {
        return waitingCharge;
    }

    public void setWaitingCharge(BigDecimal waitingCharge) {
        this.waitingCharge = waitingCharge;
    }

    public BigDecimal getTollCharge() {
        return tollCharge;
    }

    public void setTollCharge(BigDecimal tollCharge) {
        this.tollCharge = tollCharge;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public boolean isFraudFlag() {
        return fraudFlag;
    }

    public void setFraudFlag(boolean fraudFlag) {
        this.fraudFlag = fraudFlag;
    }

    public String getFraudReason() {
        return fraudReason;
    }

    public void setFraudReason(String fraudReason) {
        this.fraudReason = fraudReason;
    }
}