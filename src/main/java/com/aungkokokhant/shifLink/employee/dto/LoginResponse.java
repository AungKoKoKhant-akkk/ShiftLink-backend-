package com.aungkokokhant.shifLink.employee.dto;

import com.aungkokokhant.shifLink.employee.UserRole;

public record LoginResponse(
        Long employeeId,
        String employeeCode,
        String name,
        UserRole role
) {
}