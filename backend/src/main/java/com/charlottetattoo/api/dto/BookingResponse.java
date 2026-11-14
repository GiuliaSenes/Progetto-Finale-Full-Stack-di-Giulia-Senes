package com.charlottetattoo.api.dto;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        FlashResponse flash,
        SlotResponse slot,
        UserResponse customer,
        String note,
        String status,
        String rejectionReason,
        LocalDateTime createdAt) {
}
