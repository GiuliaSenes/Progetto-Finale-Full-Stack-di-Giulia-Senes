package com.charlottetattoo.api.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record BookingResponse(
        Long id,
        FlashResponse flash,
        SlotResponse slot,
        UserResponse customer,
        String note,
        String status,
        LocalTime confirmedTime,
        String rejectionReason,
        LocalDateTime createdAt) {
}
