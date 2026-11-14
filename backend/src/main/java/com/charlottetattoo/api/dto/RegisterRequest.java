package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Il nome è obbligatorio") String firstName,
        @NotBlank(message = "Il cognome è obbligatorio") String lastName,
        @NotBlank(message = "L'email è obbligatoria") @Email(message = "Inserisci un'email valida") String email,
        @NotBlank(message = "Il telefono è obbligatorio")
        @Pattern(regexp = "^[0-9+ ]{6,20}$", message = "Inserisci un numero di telefono valido") String phone,
        @NotBlank(message = "La password è obbligatoria")
        @Size(min = 8, message = "La password deve avere almeno 8 caratteri") String password) {
}
