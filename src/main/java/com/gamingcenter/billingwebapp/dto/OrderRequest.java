package com.gamingcenter.billingwebapp.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class OrderRequest {
    private Long sessionId;
    private Long userId;
    @NotEmpty
    private List<OrderItemRequest> items;
}
