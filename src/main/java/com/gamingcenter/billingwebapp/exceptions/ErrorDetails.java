package com.gamingcenter.billingwebapp.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetails extends RuntimeException {
    private LocalDateTime timestamp;
    private String message;
    private String details;
}
