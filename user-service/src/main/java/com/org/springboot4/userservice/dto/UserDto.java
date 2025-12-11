package com.org.springboot4.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotNull(message = "Balance is required")
    @Min(value = 0, message = "Balance must be non-negative")
    private Long balance;
    
    private LocalDateTime createdAt;
}

