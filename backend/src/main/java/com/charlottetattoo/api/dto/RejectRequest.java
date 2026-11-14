package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectRequest(
        @NotBlank(message = "Scrivi il motivo del rifiuto")
        @Size(max = 500, message = "Il motivo può avere al massimo 500 caratteri") String reason) {
}
