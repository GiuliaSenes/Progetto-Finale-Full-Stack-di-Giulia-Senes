package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank(message = "L'email è obbligatoria") @Email(message = "Inserisci un'email valida") String email) {
}
