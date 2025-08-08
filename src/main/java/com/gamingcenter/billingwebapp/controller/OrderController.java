package com.gamingcenter.billingwebapp.controller;

import com.gamingcenter.billingwebapp.dto.OrderDTO;
import com.gamingcenter.billingwebapp.dto.OrderRequest;
import com.gamingcenter.billingwebapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID')") // Anyone can place an order
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderDTO createdOrder = orderService.createOrder(request);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID') and @securityService.isUserOrAdminBasedOnOrder(#id)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')") // Admin can view orders for any session
    public ResponseEntity<List<OrderDTO>> getOrdersBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(orderService.getOrdersBySession(sessionId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID') and @securityService.isUserOrAdmin(#userId)")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}
