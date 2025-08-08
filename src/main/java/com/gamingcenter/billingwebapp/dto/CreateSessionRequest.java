package com.gamingcenter.billingwebapp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CreateSessionRequest {
    private Long userId;
    @NotNull
    private String systemId;
}
