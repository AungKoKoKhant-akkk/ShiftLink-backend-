package com.aungkokokhant.shifLink.shift;

import java.time.Duration;
import java.time.LocalTime;

public final class ShiftCalculator {

    private ShiftCalculator() {
    }

    public static int calculateWorkingMinutes(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes
    ) {
        long scheduledMinutes =
                Duration.between(startTime, endTime).toMinutes();

        if (scheduledMinutes <= 0) {
            scheduledMinutes += 24 * 60;
        }

        int workingMinutes = (int) scheduledMinutes - breakMinutes;

        if (workingMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Break time must be shorter than shift duration."
            );
        }

        return workingMinutes;
    }

    public static double calculateWorkingHours(
            LocalTime startTime,
            LocalTime endTime,
            int breakMinutes
    ) {
        return calculateWorkingMinutes(
                startTime,
                endTime,
                breakMinutes
        ) / 60.0;
    }
}
