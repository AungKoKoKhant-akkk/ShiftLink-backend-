package com.aungkokokhant.shifLink.employee.dto;

import com.aungkokokhant.shifLink.employee.EmployeeStatus;
import com.aungkokokhant.shifLink.employee.EmployeeType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequest(
        @NotBlank
        @Size(max = 30)
        String employeeCode,

        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        EmployeeType type,

        @NotBlank
        @Size(max = 100)
        String department,

        @NotNull
        EmployeeStatus status
) {
}
