package com.charlottetattoo.api.model;

/**
 * AVAILABLE   = si puo' prenotare
 * PENDING     = qualcuno l'ha richiesto e Charlotte non ha ancora risposto
 * UNAVAILABLE = non disponibile (gia' tatuato / richiesta accettata)
 */
public enum FlashStatus {
    AVAILABLE,
    PENDING,
    UNAVAILABLE
}
