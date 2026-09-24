package com.aungkokokhant.shifLink.shift.dto;

import com.aungkokokhant.shifLink.shift.ShiftStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShiftResponse(
        Long id,
        Long employeeId,
        String employeeName,
        String employeeType,
        LocalDate shiftDate,
        LocalTime startTime,
        LocalTime endTime,
        int breakMinutes,
        double workingHours,
        ShiftStatus status
) {
}
