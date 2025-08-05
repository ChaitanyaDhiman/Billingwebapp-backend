package com.gamingcenter.billingwebapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSessionRequest {
    private Long userId;
    @NotNull
    private String systemId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
