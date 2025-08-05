package com.gamingcenter.billingwebapp.controller;

import com.gamingcenter.billingwebapp.dto.BillDTO;
import com.gamingcenter.billingwebapp.dto.CreateSessionRequest;
import com.gamingcenter.billingwebapp.dto.SessionDTO;
import com.gamingcenter.billingwebapp.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/api/sessions")
@PreAuthorize("hasRole('ADMIN')") // Only Admins can start/end sessions
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/start")
    public ResponseEntity<SessionDTO> startSession(@Valid @RequestBody CreateSessionRequest request) {
        SessionDTO startedSession = sessionService.startSession(request);
        return new ResponseEntity<>(startedSession, HttpStatus.CREATED);
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<BillDTO> endSession(@PathVariable Long sessionId) {
        BillDTO bill = sessionService.endSession(sessionId);
        return ResponseEntity.ok(bill);
    }

    @GetMapping
    public ResponseEntity<List<SessionDTO>> getAllSessions() {
        return ResponseEntity.ok(sessionService.getAllSessions());
    }

    @GetMapping("/active")
    public ResponseEntity<List<SessionDTO>> getActiveSessions() {
        return ResponseEntity.ok(sessionService.getActiveSessions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }
}
