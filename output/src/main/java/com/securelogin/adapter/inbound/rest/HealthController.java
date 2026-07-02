package com.securelogin.adapter.inbound.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Health check endpoint for liveness/readiness probes.
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * Returns the current health status of the service.
     *
     * @return 200 OK with status "UP"
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
