package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.dto.ContractRequest;
import com.moveinsync.fleetbilling.entity.Contract;
import com.moveinsync.fleetbilling.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    private final ContractService service;

    public ContractController(ContractService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contract create(
            @Valid @RequestBody ContractRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public List<Contract> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Contract getById(@PathVariable Long id) {
        return service.getById(id);
    }
}