package com.aungkokokhant.shifLink.swap.dto;

import com.aungkokokhant.shifLink.swap.SwapRequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateSwapRequestStatus(
        @NotNull SwapRequestStatus status,
        Long replacementEmployeeId
) {
}