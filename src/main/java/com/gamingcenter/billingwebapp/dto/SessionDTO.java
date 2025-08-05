package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.SessionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public BigDecimal getHourlyRateAtStart() {
        return hourlyRateAtStart;
    }

    public void setHourlyRateAtStart(BigDecimal hourlyRateAtStart) {
        this.hourlyRateAtStart = hourlyRateAtStart;
    }

    public Integer getActualDurationMinutes() {
        return actualDurationMinutes;
    }

    public void setActualDurationMinutes(Integer actualDurationMinutes) {
        this.actualDurationMinutes = actualDurationMinutes;
    }

    public BigDecimal getTotalHourlyCharge() {
        return totalHourlyCharge;
    }

    public void setTotalHourlyCharge(BigDecimal totalHourlyCharge) {
        this.totalHourlyCharge = totalHourlyCharge;
    }
}
