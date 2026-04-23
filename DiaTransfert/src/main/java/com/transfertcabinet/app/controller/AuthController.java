package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.security.AuthenticationService;
import com.transfertcabinet.app.security.JwtAuthenticationRequest;
import com.transfertcabinet.app.security.JwtAuthenticationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/auth")  // ✅ CORRIGÉ : Avec /api
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody JwtAuthenticationRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        JwtAuthenticationResponse response = authenticationService.authenticate(request);
        log.info("Login successful for user: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<JwtAuthenticationResponse> register(@Valid @RequestBody JwtAuthenticationRequest request) {
        log.info("Register attempt for user: {}", request.getUsername());
        JwtAuthenticationResponse response = authenticationService.register(request);
        log.info("User registered successfully: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
}