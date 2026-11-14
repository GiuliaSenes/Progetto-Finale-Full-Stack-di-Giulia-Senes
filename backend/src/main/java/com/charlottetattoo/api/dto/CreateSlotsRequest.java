package com.charlottetattoo.api.dto;

import com.charlottetattoo.api.model.DayPeriod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record CreateSlotsRequest(
        @NotNull(message = "Scegli una data") LocalDate date,
        @NotEmpty(message = "Scegli mattina, pomeriggio o entrambi") List<DayPeriod> periods) {
}
