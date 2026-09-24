package com.moveinsync.fleetbilling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @Column(nullable = false)
    private String chargingModel;

    private String pricingType;

    @Column(precision = 19, scale = 2)
    private BigDecimal monthlyFee;

    @Column(precision = 19, scale = 3)
    private BigDecimal includedKm;

    @Column(precision = 19, scale = 2)
    private BigDecimal overageRate;

    @Column(precision = 19, scale = 2)
    private BigDecimal perKmRate;

    @Column(precision = 19, scale = 2)
    private BigDecimal perTripRate;

    @Column(precision = 19, scale = 2)
    private BigDecimal nightChargePerTrip;

    @Column(precision = 19, scale = 2)
    private BigDecimal waitingChargePerHour;

    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @JsonIgnore
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("minKm ASC")
    private List<ContractSlab> slabs = new ArrayList<>();

    public Contract() {
    }

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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

    public List<ContractSlab> getSlabs() {
        return slabs;
    }

    public void setSlabs(List<ContractSlab> slabs) {
        this.slabs = slabs;
    }
}