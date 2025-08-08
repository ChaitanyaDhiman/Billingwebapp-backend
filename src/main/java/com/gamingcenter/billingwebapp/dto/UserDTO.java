package com.gamingcenter.billingwebapp.dto;

import com.gamingcenter.billingwebapp.model.UserRole;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private UserRole role;
    private String email;
    private String phoneNumber;
    private BigDecimal balance; // For prepaid users, might be sensitive only shows to admin or user themselves
}
