package com.aungkokokhant.shifLink.shift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftRequest(
        @NotNull
        Long employeeId,

        @NotNull
        LocalDate shiftDate,

        @NotNull
        LocalTime startTime,

        @NotNull
        LocalTime endTime,

        @Min(0)
        int breakMinutes
) {
}
