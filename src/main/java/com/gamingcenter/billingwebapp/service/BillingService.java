package com.gamingcenter.billingwebapp.service;

import com.gamingcenter.billingwebapp.dto.BillDTO;
import com.gamingcenter.billingwebapp.dto.PaymentRequest;
import com.gamingcenter.billingwebapp.model.*;
import com.gamingcenter.billingwebapp.repository.BillRepository;
import com.gamingcenter.billingwebapp.repository.OrderRepository;
import com.gamingcenter.billingwebapp.repository.SessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final BillRepository billRepository;
    private final SessionRepository sessionRepository;
    private final OrderRepository orderRepository;
    private final UserService userService; //To deduct balance from prepaid users
    private final ModelMapper modelMapper;

    @Transactional
    public BillDTO generateBillForSession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));
        if(session.getStatus() != SessionStatus.COMPLETED) {
            throw new IllegalArgumentException("Bill can only be generated if session is completed");
        }
        //Check if bill already exists for this session
        Optional<Bill> existingBill = billRepository.findById(sessionId);
        if(existingBill.isPresent()) {
            return modelMapper.map(existingBill.get(), BillDTO.class);
        }

        BigDecimal sessionCharge = session.getTotalHourlyCharge();
        if(sessionCharge == null) {
            // Recalculate if not set (should be set by SessionService.endSession)
            long durationMinutes = Duration.between(session.getStartTime(), session.getEndTime()).toMinutes();
            sessionCharge = session.getHourlyRateAtStart()
                    .multiply(BigDecimal.valueOf(durationMinutes / 60.0));
            session.setTotalHourlyCharge(sessionCharge);
            sessionRepository.save(session);
        }

        List<Order> snackOrder = orderRepository.findBySessionId(sessionId);
        BigDecimal snackCharge = snackOrder.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = sessionCharge.add(snackCharge);

        Bill bill = new Bill();
        bill.setSession(session);
        bill.setUser(session.getUser());
        bill.setBillDate(LocalDateTime.now());
        bill.setSessionCharge(sessionCharge);
        bill.setSnackCharge(snackCharge);
        bill.setTotalAmount(totalAmount);
        bill.setPaymentStatus(PaymentStatus.UNPAID);

        Bill savedBill = billRepository.save(bill);
        return modelMapper.map(savedBill, BillDTO.class);
    }

    public BillDTO getBillById(Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with id: " + id));
        return modelMapper.map(bill, BillDTO.class);
    }

    public List<BillDTO> getBillsForUser(Long userId) {
        return billRepository.findByUserId(userId).stream()
                .map(bill -> modelMapper.map(bill, BillDTO.class))
                .collect(Collectors.toList());
    }

    public List<BillDTO> getAllBills() {
        return billRepository.findAll().stream()
                .map(bill -> modelMapper.map(bill, BillDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public BillDTO processBill(PaymentRequest request) {
        Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with id: " + request.getBillId()));
        if(bill.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("Bill is already fully paid");
        }

        BigDecimal paymentAmount = request.getAmount();
        BigDecimal remainingAmount = bill.getTotalAmount().subtract(paymentAmount);

        if(paymentAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive.");
        }
        if(paymentAmount.compareTo(remainingAmount) > 0) {
            // If payment exceeds remaining amount, only pay the remaining
            paymentAmount = remainingAmount;
        }
        bill.setPaidAmount(bill.getPaidAmount().add(paymentAmount));
        bill.setPaymentMethod(request.getPaymentMethod());

        if(bill.getPaidAmount().compareTo(bill.getTotalAmount()) >= 0){
            bill.setPaymentStatus(PaymentStatus.PAID);
        }else {
            bill.setPaymentStatus(PaymentStatus.PARTIAL);
        }

        //For prepaid users, deduct form their balance
        if(bill.getUser() != null && bill.getUser().getRole() == UserRole.PREPAID) {
            userService.deductBalance(bill.getUser().getId(), paymentAmount);
        }

        Bill updatedBill = billRepository.save(bill);
        return modelMapper.map(updatedBill, BillDTO.class);
    }
}
