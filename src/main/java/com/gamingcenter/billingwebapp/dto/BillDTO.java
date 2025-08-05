package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillDTO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String username;
    private LocalDateTime billDate;
    private BigDecimal sessionCharge;
    private BigDecimal snacksCharge;
    private BigDecimal totalAmount;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private BigDecimal paidAmount;
}
