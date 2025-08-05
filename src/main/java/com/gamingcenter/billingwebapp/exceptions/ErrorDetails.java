package com.gamingcenter.billingwebapp.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails extends RuntimeException {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
