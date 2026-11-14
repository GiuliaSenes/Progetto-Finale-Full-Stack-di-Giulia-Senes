package com.charlottetattoo.api.dto;

import java.time.LocalDate;

public record SlotResponse(Long id, LocalDate date, String period, String status) {
}
