package com.aungkokokhant.shifLink.swap.dto;


import com.aungkokokhant.shifLink.swap.SwapRequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record SwapRequestResponse(
        Long id,
        Long shiftId,
        String employeeName,
        Long replacementEmployeeId,
        String replacementEmployeeName,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        String reason,
        SwapRequestStatus status,
        LocalDateTime requestedAt
) {
}