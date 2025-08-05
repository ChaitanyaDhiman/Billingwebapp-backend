package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.SystemStatus;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GamingSystemDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal hourlyRate;
    private SystemStatus status;
}
