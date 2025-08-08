package com.gamingcenter.billingwebapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class OrderItemRequest {
    @NotNull
    private Long productId;
    @Min(1)
    private Integer quantity;
}
