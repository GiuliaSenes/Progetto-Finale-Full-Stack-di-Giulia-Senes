package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequest(
        @NotBlank(message = "Il nome è obbligatorio") String firstName,
        @NotBlank(message = "Il cognome è obbligatorio") String lastName,
        @NotBlank(message = "Il telefono è obbligatorio")
        @Pattern(regexp = "^[0-9+ ]{6,20}$", message = "Inserisci un numero di telefono valido") String phone) {
}
