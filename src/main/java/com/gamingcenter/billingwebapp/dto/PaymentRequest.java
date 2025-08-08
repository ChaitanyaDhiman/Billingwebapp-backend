package com.gamingcenter.billingwebapp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class PaymentRequest {
    @NotNull
    private Long billId;
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    private String paymentMethod;
}
