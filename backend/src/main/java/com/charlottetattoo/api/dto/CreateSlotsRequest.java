package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record CreateSlotsRequest(
        @NotNull(message = "Scegli una data") LocalDate date,
        @NotEmpty(message = "Scegli almeno una fascia oraria") List<LocalTime> startTimes) {
}
