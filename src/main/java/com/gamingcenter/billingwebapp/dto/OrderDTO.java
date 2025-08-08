package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.OrderStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class OrderDTO {
    private Long id;
    private Long sessionId;
    private Long userId;
    private String username;
    private LocalDateTime orderTime;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private List<OrderItemDTO> items;
}
