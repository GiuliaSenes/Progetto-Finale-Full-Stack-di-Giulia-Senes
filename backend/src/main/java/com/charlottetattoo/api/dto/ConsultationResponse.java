package com.charlottetattoo.api.dto;

import java.time.LocalDateTime;

public record ConsultationResponse(
        Long id,
        UserResponse customer,
        String message,
        String referenceImageUrl,
        String status,
        LocalDateTime createdAt) {
}
