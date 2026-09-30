package com.moveinsync.fleetbilling.controller;

import com.moveinsync.fleetbilling.security.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public String getToken() {

        return jwtService.generateToken();
    }
}