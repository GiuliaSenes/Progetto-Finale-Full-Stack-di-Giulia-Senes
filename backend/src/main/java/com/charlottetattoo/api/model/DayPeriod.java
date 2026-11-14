package com.charlottetattoo.api.model;

import java.time.LocalTime;

/**
 * Metà giornata in cui Charlotte è disponibile.
 * L'orario preciso non lo sceglie il cliente: lo decide Charlotte quando accetta la richiesta,
 * ma deve cadere dentro la fascia (da "start" incluso a "end" escluso).
 */
public enum DayPeriod {
    MORNING(LocalTime.of(9, 0), LocalTime.of(13, 0)),
    AFTERNOON(LocalTime.of(13, 0), LocalTime.of(20, 0));

    private final LocalTime start;
    private final LocalTime end;

    DayPeriod(LocalTime start, LocalTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public boolean contains(LocalTime time) {
        return !time.isBefore(start) && time.isBefore(end);
    }

    // Nome in italiano con l'articolo ("la mattina", "il pomeriggio"), usato nei messaggi di errore
    public String labelWithArticle() {
        return this == MORNING ? "la mattina" : "il pomeriggio";
    }
}
