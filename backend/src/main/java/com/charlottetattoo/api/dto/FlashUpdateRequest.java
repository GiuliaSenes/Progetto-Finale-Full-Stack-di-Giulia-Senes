package com.charlottetattoo.api.dto;

import com.charlottetattoo.api.model.FlashStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FlashUpdateRequest(
        @NotBlank(message = "Scrivi la dimensione del flash") String size,
        @NotNull(message = "Scegli lo stato del flash") FlashStatus status) {
}
