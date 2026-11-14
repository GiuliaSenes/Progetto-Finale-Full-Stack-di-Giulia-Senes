package com.charlottetattoo.api.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/** Charlotte accetta la richiesta e comunica l'orario preciso. */
public record AcceptRequest(
        @NotNull(message = "Scrivi l'orario dell'appuntamento") LocalTime time) {
}
