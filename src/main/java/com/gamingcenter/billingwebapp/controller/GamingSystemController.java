package com.gamingcenter.billingwebapp.controller;

import com.gamingcenter.billingwebapp.dto.GamingSystemDTO;
import com.gamingcenter.billingwebapp.model.SystemStatus;
import com.gamingcenter.billingwebapp.service.GamingSystemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/systems")
@PreAuthorize("hasRole('ADMIN')")
public class GamingSystemController {
    private final GamingSystemService systemService;

    public GamingSystemController(GamingSystemService systemService) {
        this.systemService = systemService;
    }

    @PostMapping
    public ResponseEntity<GamingSystemDTO> createSystem(@Valid @RequestBody GamingSystemDTO systemDTO) {
        GamingSystemDTO createdSystem = systemService.createSystem(systemDTO);
        return new ResponseEntity<>(createdSystem, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GamingSystemDTO>> getAllSystems() {
        return ResponseEntity.ok(systemService.getAllSystems());
    }

    @GetMapping("/available")
    public ResponseEntity<List<GamingSystemDTO>> getAvailableSystems() {
        return ResponseEntity.ok(systemService.getAvailableSystems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GamingSystemDTO> getSystemById(@PathVariable Long id) {
        return ResponseEntity.ok(systemService.getSystemById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<GamingSystemDTO> updateSystemStatus(@PathVariable Long id, @RequestParam SystemStatus status) {
        GamingSystemDTO updatedSystem = systemService.updateSystemStatus(id, status);
        return ResponseEntity.ok(updatedSystem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSystem(@PathVariable Long id) {
        systemService.deleteSystem(id);
        return ResponseEntity.noContent().build();
    }

}
