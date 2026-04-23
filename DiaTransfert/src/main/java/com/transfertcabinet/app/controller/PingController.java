package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.service.CashOperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ping")
@RequiredArgsConstructor
public class PingController {

    private final CashOperationService cashOperationService;  // ← TEST D'INJECTION

    @GetMapping
    public String ping() {
        return "pong - service injecté: " + (cashOperationService != null);
    }
}