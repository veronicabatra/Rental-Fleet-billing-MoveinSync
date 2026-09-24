package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.dto.BillingRequest;
import com.moveinsync.fleetbilling.dto.BillingResponse;
import com.moveinsync.fleetbilling.service.BillingService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing")
@CrossOrigin(origins = "*")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/run")
    public BillingResponse runBilling(
            @RequestBody BillingRequest request
    ) {
        return billingService.runBilling(request);
    }
}