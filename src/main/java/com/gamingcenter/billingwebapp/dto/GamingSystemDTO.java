package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.SystemStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class GamingSystemDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal hourlyRate;
    private SystemStatus status;
}
