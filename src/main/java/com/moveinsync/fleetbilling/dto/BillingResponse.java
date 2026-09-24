package com.moveinsync.fleetbilling.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class BillingResponse {

    private Long billingRunId;
    private Long vehicleId;
    private String billingMonth;

    private BigDecimal totalAmount;
    private int tripCount;
    private int flaggedTripCount;

    private BigDecimal totalDutyKm;
    private BigDecimal totalDeadKm;
    private BigDecimal totalChargeableKm;

    private BigDecimal baseCharges;
    private BigDecimal fixedAllocationTotal;
    private BigDecimal nightCharges;
    private BigDecimal waitingCharges;
    private BigDecimal tollCharges;

    private BigDecimal fixedFeePool;
    private BigDecimal reconciliationDifference;

    private String reconciliationStatus;
    private String status;

    private ContractInfo contract;
    private List<TripBillInfo> tripBills;

    public BillingResponse() {
    }

    public BillingResponse(
            Long billingRunId,
            Long vehicleId,
            String billingMonth,
            BigDecimal totalAmount,
            int tripCount,
            int flaggedTripCount,
            BigDecimal totalDutyKm,
            BigDecimal totalDeadKm,
            BigDecimal totalChargeableKm,
            BigDecimal baseCharges,
            BigDecimal fixedAllocationTotal,
            BigDecimal nightCharges,
            BigDecimal waitingCharges,
            BigDecimal tollCharges,
            BigDecimal fixedFeePool,
            BigDecimal reconciliationDifference,
            String reconciliationStatus,
            String status,
            ContractInfo contract,
            List<TripBillInfo> tripBills
    ) {
        this.billingRunId = billingRunId;
        this.vehicleId = vehicleId;
        this.billingMonth = billingMonth;
        this.totalAmount = totalAmount;
        this.tripCount = tripCount;
        this.flaggedTripCount = flaggedTripCount;
        this.totalDutyKm = totalDutyKm;
        this.totalDeadKm = totalDeadKm;
        this.totalChargeableKm = totalChargeableKm;
        this.baseCharges = baseCharges;
        this.fixedAllocationTotal = fixedAllocationTotal;
        this.nightCharges = nightCharges;
        this.waitingCharges = waitingCharges;
        this.tollCharges = tollCharges;
        this.fixedFeePool = fixedFeePool;
        this.reconciliationDifference = reconciliationDifference;
        this.reconciliationStatus = reconciliationStatus;
        this.status = status;
        this.contract = contract;
        this.tripBills = tripBills;
    }

    public Long getBillingRunId() {
        return billingRunId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public int getTripCount() {
        return tripCount;
    }

    public int getFlaggedTripCount() {
        return flaggedTripCount;
    }

    public BigDecimal getTotalDutyKm() {
        return totalDutyKm;
    }

    public BigDecimal getTotalDeadKm() {
        return totalDeadKm;
    }

    public BigDecimal getTotalChargeableKm() {
        return totalChargeableKm;
    }

    public BigDecimal getBaseCharges() {
        return baseCharges;
    }

    public BigDecimal getFixedAllocationTotal() {
        return fixedAllocationTotal;
    }

    public BigDecimal getNightCharges() {
        return nightCharges;
    }

    public BigDecimal getWaitingCharges() {
        return waitingCharges;
    }

    public BigDecimal getTollCharges() {
        return tollCharges;
    }

    public BigDecimal getFixedFeePool() {
        return fixedFeePool;
    }

    public BigDecimal getReconciliationDifference() {
        return reconciliationDifference;
    }

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    public String getStatus() {
        return status;
    }

    public ContractInfo getContract() {
        return contract;
    }

    public List<TripBillInfo> getTripBills() {
        return tripBills;
    }


    // ==========================
    // CONTRACT INFORMATION
    // ==========================

    public static class ContractInfo {

        private Long contractId;
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

        public ContractInfo() {
        }

        public ContractInfo(
                Long contractId,
                String chargingModel,
                String pricingType,
                BigDecimal monthlyFee,
                BigDecimal includedKm,
                BigDecimal overageRate,
                BigDecimal perKmRate,
                BigDecimal perTripRate,
                BigDecimal nightChargePerTrip,
                BigDecimal waitingChargePerHour,
                LocalDate validFrom,
                LocalDate validTo
        ) {
            this.contractId = contractId;
            this.chargingModel = chargingModel;
            this.pricingType = pricingType;
            this.monthlyFee = monthlyFee;
            this.includedKm = includedKm;
            this.overageRate = overageRate;
            this.perKmRate = perKmRate;
            this.perTripRate = perTripRate;
            this.nightChargePerTrip = nightChargePerTrip;
            this.waitingChargePerHour = waitingChargePerHour;
            this.validFrom = validFrom;
            this.validTo = validTo;
        }

        public Long getContractId() {
            return contractId;
        }

        public String getChargingModel() {
            return chargingModel;
        }

        public String getPricingType() {
            return pricingType;
        }

        public BigDecimal getMonthlyFee() {
            return monthlyFee;
        }

        public BigDecimal getIncludedKm() {
            return includedKm;
        }

        public BigDecimal getOverageRate() {
            return overageRate;
        }

        public BigDecimal getPerKmRate() {
            return perKmRate;
        }

        public BigDecimal getPerTripRate() {
            return perTripRate;
        }

        public BigDecimal getNightChargePerTrip() {
            return nightChargePerTrip;
        }

        public BigDecimal getWaitingChargePerHour() {
            return waitingChargePerHour;
        }

        public LocalDate getValidFrom() {
            return validFrom;
        }

        public LocalDate getValidTo() {
            return validTo;
        }
    }


    // ==========================
    // TRIP BILL INFORMATION
    // ==========================

    public static class TripBillInfo {

        private Long tripBillId;
        private Long tripId;
        private LocalDate tripDate;

        private BigDecimal dutyKm;
        private BigDecimal deadKm;
        private BigDecimal totalKm;

        private BigDecimal baseCharge;
        private BigDecimal fixedAllocation;
        private BigDecimal nightCharge;
        private BigDecimal waitingCharge;
        private BigDecimal tollCharge;
        private BigDecimal totalAmount;

        private boolean fraudFlag;
        private String fraudReason;

        private String chargingModel;
        private String pricingType;

        public TripBillInfo() {
        }

        public TripBillInfo(
                Long tripBillId,
                Long tripId,
                LocalDate tripDate,
                BigDecimal dutyKm,
                BigDecimal deadKm,
                BigDecimal totalKm,
                BigDecimal baseCharge,
                BigDecimal fixedAllocation,
                BigDecimal nightCharge,
                BigDecimal waitingCharge,
                BigDecimal tollCharge,
                BigDecimal totalAmount,
                boolean fraudFlag,
                String fraudReason,
                String chargingModel,
                String pricingType
        ) {
            this.tripBillId = tripBillId;
            this.tripId = tripId;
            this.tripDate = tripDate;
            this.dutyKm = dutyKm;
            this.deadKm = deadKm;
            this.totalKm = totalKm;
            this.baseCharge = baseCharge;
            this.fixedAllocation = fixedAllocation;
            this.nightCharge = nightCharge;
            this.waitingCharge = waitingCharge;
            this.tollCharge = tollCharge;
            this.totalAmount = totalAmount;
            this.fraudFlag = fraudFlag;
            this.fraudReason = fraudReason;
            this.chargingModel = chargingModel;
            this.pricingType = pricingType;
        }

        public Long getTripBillId() {
            return tripBillId;
        }

        public Long getTripId() {
            return tripId;
        }

        public LocalDate getTripDate() {
            return tripDate;
        }

        public BigDecimal getDutyKm() {
            return dutyKm;
        }

        public BigDecimal getDeadKm() {
            return deadKm;
        }

        public BigDecimal getTotalKm() {
            return totalKm;
        }

        public BigDecimal getBaseCharge() {
            return baseCharge;
        }

        public BigDecimal getFixedAllocation() {
            return fixedAllocation;
        }

        public BigDecimal getNightCharge() {
            return nightCharge;
        }

        public BigDecimal getWaitingCharge() {
            return waitingCharge;
        }

        public BigDecimal getTollCharge() {
            return tollCharge;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public boolean isFraudFlag() {
            return fraudFlag;
        }

        public String getFraudReason() {
            return fraudReason;
        }

        public String getChargingModel() {
            return chargingModel;
        }

        public String getPricingType() {
            return pricingType;
        }
    }
}