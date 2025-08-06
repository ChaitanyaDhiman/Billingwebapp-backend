package com.gamingcenter.billingwebapp.service;

import com.gamingcenter.billingwebapp.dto.BillDTO;
import com.gamingcenter.billingwebapp.dto.CreateSessionRequest;
import com.gamingcenter.billingwebapp.dto.SessionDTO;

import com.gamingcenter.billingwebapp.model.*;
import com.gamingcenter.billingwebapp.repository.GamingSystemRepository;
import com.gamingcenter.billingwebapp.repository.SessionRepository;
import com.gamingcenter.billingwebapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GamingSystemRepository systemRepository;
    private final BillingService billingService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ModelMapper modelMapper;

    @Transactional
    public SessionDTO startSession(CreateSessionRequest request) {
        GamingSystem system = systemRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("System not found by id: " + request.getUserId()));

        if(system.getStatus() == SystemStatus.IN_USE){
            throw new IllegalArgumentException("Game system " + system.getName() + " is already in use");
        }

        Optional<Session> activeSessionOnSystem = sessionRepository.findBySystemIdAndStatus(system.getId(), SessionStatus.ACTIVE);
        if(activeSessionOnSystem.isPresent()){
            throw new IllegalArgumentException("There is already an active session on system: " + system.getName());
        }

        User user = null;
        if(request.getUserId() != null){
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found by id: " + request.getUserId()));
        }
        Session session = new Session();
        session.setUser(user);
        session.setSystem(system);
        session.setStartTime(LocalDateTime.now());
        session.setHourlyRateAtStart(system.getHourlyRate());
        session.setStatus(SessionStatus.ACTIVE);

        system.setStatus(SystemStatus.IN_USE);
        sessionRepository.save(session);

        Session savedSession = sessionRepository.save(session);
        return modelMapper.map(savedSession, SessionDTO.class);
    }

    @Transactional
    public BillDTO endSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found by id: " + sessionId));
        if(session.getStatus() != SessionStatus.ACTIVE){
            throw new IllegalArgumentException("Session is not active and cannot be ended: " + sessionId);
        }

        session.setEndTime(LocalDateTime.now());
        session.setStatus(SessionStatus.COMPLETED);

        //Calculate duration and charge
        long duraionMinutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
        session.setActualDurationMinutes((int) duraionMinutes);

        //Calculate session charge based on hourly rates
        BigDecimal totalHourlyCharge = session.getHourlyRateAtStart()
                .multiply(BigDecimal.valueOf(duraionMinutes / 60.0));
        session.setTotalHourlyCharge(totalHourlyCharge);

        GamingSystem system = session.getSystem();
        system.setStatus(SystemStatus.AVAILABLE);
        systemRepository.save(system);

        sessionRepository.save(session);
        //Generate the bill
        return billingService.generateBillForSession(session.getId());
    }

    public List<SessionDTO> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapSessionToDTO)
                .collect(Collectors.toList());
    }

    public List<SessionDTO> getActiveSessions() {
        return sessionRepository.findAll().stream()
                .map(this::mapSessionToDTO)
                .collect(Collectors.toList());
    }

    public SessionDTO getSessionById(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found by id: " + sessionId));
        return mapSessionToDTO(session);
    }

    // This method will be scheduled to run periodically for prepaid user notifications
    // You might want to adjust the fixedRate based on how often you want to check
    // For production, consider external messaging (e.g., Kafka, RabbitMQ) for real-time notifications
    @Scheduled(fixedRate = 60000)// Runs every 60 seconds (1 minute)
    public void checkPrepaidSessionExpiry() {
        List<Session> activePrepaidSessions = sessionRepository.findByStatus(SessionStatus.ACTIVE)
                .stream()
                .filter(session -> session.getUser() != null && session.getUser().getRole() == UserRole.PREPAID)
                .collect(Collectors.toList());

        for(Session session : activePrepaidSessions){
            long elapsedTime = Duration.between(session.getStartTime(), LocalDateTime.now()).toMinutes();
            User prepaidUser = session.getUser();

            // Example: Notify if 5 minutes remaining based on current balance
            // This assumes a simple calculation: balance / hourlyRate * 60 minutes
            if(prepaidUser.getBalance() != null && session.getHourlyRateAtStart() != null
            && session.getHourlyRateAtStart().compareTo(prepaidUser.getBalance()) >= 0){
                BigDecimal remainingTimeHours = prepaidUser.getBalance().divide(session.getHourlyRateAtStart(), 2, BigDecimal.ROUND_HALF_UP);
                long remainingMinutes = remainingTimeHours.multiply(BigDecimal.valueOf(60)).longValue();

                if(remainingMinutes > 0 && remainingMinutes <=5){
                    String notificationMessage = "You have approximately " + remainingMinutes + " minutes remaining on your session (System: " + session.getSystem().getName() + ")!";
                    messagingTemplate.convertAndSend("/topic/user/" + prepaidUser.getId() + "/notifications", notificationMessage);
                    // In a real application, send a push notification, SMS, or email
                }else if(remainingMinutes <= 0){
                    // Automatically end session if balance is depleted and session is still active
                    if(session.getStatus() == SessionStatus.ACTIVE){
                        String notificationMessage = "Prepaid user " + prepaidUser.getUsername() + " (Session ID: " + session.getId() +
                                ") balance depleted. Ending session automatically.";
                        messagingTemplate.convertAndSend("/topic/user/" + prepaidUser.getId() + "/notifications", notificationMessage);
                        endSession(session.getId()); // Call end session
                    }
                }
            }
        }
    }

    private SessionDTO mapSessionToDTO(Session session) {
        SessionDTO dto = modelMapper.map(session, SessionDTO.class);
        if (session.getUser() != null) {
            dto.setUserId(session.getUser().getId());
            dto.setUsername(session.getUser().getUsername());
        }
        if (session.getSystem() != null) {
            dto.setSystemId(session.getSystem().getId());
            dto.setSystemName(session.getSystem().getName());
        }
        return dto;
    }

}
