package com.gamingcenter.billingwebapp.controller;

import com.gamingcenter.billingwebapp.dto.UserDTO;
import com.gamingcenter.billingwebapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID') and @securityService.isUserOrAdmin(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping("/{id}/topup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> topUpPrepaidUser(@PathVariable("id") Long id, @RequestBody BigDecimal amount) {
        return ResponseEntity.ok(userService.topUpPrepaidUserBalance(id, amount));
    }
}
