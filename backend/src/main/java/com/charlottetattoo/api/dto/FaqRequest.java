package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FaqRequest(
        @NotBlank(message = "Scrivi la domanda")
        @Size(max = 300, message = "La domanda può avere al massimo 300 caratteri") String question,
        @NotBlank(message = "Scrivi la risposta")
        @Size(max = 2000, message = "La risposta può avere al massimo 2000 caratteri") String answer) {
}
