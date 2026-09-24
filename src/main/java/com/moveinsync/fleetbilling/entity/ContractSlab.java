package com.moveinsync.fleetbilling.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "contract_slabs")
public class ContractSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @Column(nullable = false, precision = 19, scale = 3)
    private BigDecimal minKm;

    @Column(precision = 19, scale = 3)
    private BigDecimal maxKm;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal ratePerKm;

    public ContractSlab() {
    }

    public Long getId() {
        return id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
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