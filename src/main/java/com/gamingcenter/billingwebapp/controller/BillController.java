package com.gamingcenter.billingwebapp.controller;

import com.gamingcenter.billingwebapp.dto.BillDTO;
import com.gamingcenter.billingwebapp.dto.PaymentRequest;
import com.gamingcenter.billingwebapp.service.BillingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillingService billingService;

    public BillController(BillingService billingService) {
        this.billingService = billingService;
    }

    // This endpoint would typically be triggered by SessionService after a session ends.
    // Making it an admin endpoint here for explicit demonstration.
    @PostMapping("/generate/{sessionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BillDTO> generateBillForSession(@PathVariable Long sessionId) {
        BillDTO bill = billingService.generateBillForSession(sessionId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID') and @securityService.isUserOrAdminBasedOnBill(#id)")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billingService.getBillById(id));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PREPAID', 'POSTPAID') and @securityService.isUserOrAdmin(#userId)")
    public ResponseEntity<List<BillDTO>> getBillsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(billingService.getBillsForUser(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billingService.getAllBills());
    }

    @PostMapping("/pay")
    @PreAuthorize("hasRole('ADMIN') or hasAnyRole('PREPAID', 'POSTPAID') and @securityService.isUserOrAdminBasedOnBill(#request.billId)")
    public ResponseEntity<BillDTO> processPayment(@Valid @RequestBody PaymentRequest request) {
        BillDTO updatedBill = billingService.processBill(request);
        return ResponseEntity.ok(updatedBill);
    }
}
