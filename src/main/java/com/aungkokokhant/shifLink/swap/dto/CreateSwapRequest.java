package com.aungkokokhant.shifLink.swap.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSwapRequest(
        @NotNull Long shiftId,

        @NotBlank
        @Size(max = 500)
        String reason
) {
}