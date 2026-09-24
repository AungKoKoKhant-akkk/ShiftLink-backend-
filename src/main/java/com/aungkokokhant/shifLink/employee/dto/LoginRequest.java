package com.aungkokokhant.shifLink.employee.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String employeeCode,

        @NotBlank
        String password
) {
}