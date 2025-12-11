package com.org.springboot4.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CreateUserRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotNull(message = "Initial balance is required")
    @Min(value = 0, message = "Initial balance must be non-negative")
    private Long initialBalance;
}

