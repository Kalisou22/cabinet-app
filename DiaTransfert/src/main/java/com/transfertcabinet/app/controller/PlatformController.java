package com.transfertcabinet.app.controller;

import com.transfertcabinet.app.entity.Platform;
import com.transfertcabinet.app.service.PlatformService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/platforms")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping
    public ResponseEntity<List<Platform>> getAllPlatforms(
            @RequestParam(required = false, defaultValue = "true") boolean activeOnly) {
        log.info("REST request to get platforms, activeOnly: {}", activeOnly);
        List<Platform> platforms = activeOnly ? platformService.findAllActive() : platformService.findAll();
        return ResponseEntity.ok(platforms);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Platform> getPlatform(@PathVariable Long id) {
        log.info("REST request to get platform: {}", id);
        Platform platform = platformService.findById(id);
        return ResponseEntity.ok(platform);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Platform> createPlatform(@Valid @RequestBody Platform platform) {
        log.info("REST request to create platform: {}", platform.getName());
        Platform created = platformService.create(platform);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Platform> updatePlatform(@PathVariable Long id, @Valid @RequestBody Platform platform) {
        log.info("REST request to update platform: {}", id);
        Platform updated = platformService.update(id, platform);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> togglePlatformStatus(@PathVariable Long id) {
        log.info("REST request to toggle platform status: {}", id);
        platformService.toggleStatus(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlatform(@PathVariable Long id) {
        log.info("REST request to delete platform: {}", id);
        platformService.delete(id);
        return ResponseEntity.noContent().build();
    }
}