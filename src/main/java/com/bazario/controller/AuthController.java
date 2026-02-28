package com.bazario.controller;

import com.bazario.dto.request.LoginRequest;
import com.bazario.dto.request.RegisterRequest;
import com.bazario.dto.response.AuthResponse;
import com.bazario.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Customer registration and login (US-01, US-02)")
public class AuthController {

    private final AuthService authService;

    /**
     * US-01 – Customer registration.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new customer account")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    /**
     * US-02 – Secure login.
     */
    @PostMapping("/login")
    @Operation(summary = "Log in and receive a JWT access token")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
