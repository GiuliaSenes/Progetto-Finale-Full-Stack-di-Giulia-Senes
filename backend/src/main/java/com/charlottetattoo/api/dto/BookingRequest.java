package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookingRequest(
        @NotNull(message = "Scegli un flash") Long flashId,
        @NotNull(message = "Scegli una data e un orario") Long slotId,
        @Size(max = 500, message = "La nota può avere al massimo 500 caratteri") String note) {
}
