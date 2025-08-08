package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.SessionStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class SessionDTO {
    private Long id;
    private Long userId;
    private Long systemId;
    private String username;
    private String systemName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private SessionStatus status;
    private BigDecimal hourlyRateAtStart;
    private Integer actualDurationMinutes;
    private BigDecimal totalHourlyCharge;
}
