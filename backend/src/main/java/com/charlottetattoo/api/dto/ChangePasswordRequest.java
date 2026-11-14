package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Inserisci la password attuale") String currentPassword,
        @NotBlank(message = "Inserisci la nuova password")
        @Size(min = 8, message = "La nuova password deve avere almeno 8 caratteri") String newPassword) {
}
