package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.BookingRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.Booking;
import com.charlottetattoo.api.model.BookingStatus;
import com.charlottetattoo.api.model.DayPeriod;
import com.charlottetattoo.api.model.Flash;
import com.charlottetattoo.api.model.FlashStatus;
import com.charlottetattoo.api.model.Role;
import com.charlottetattoo.api.model.SlotStatus;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.BookingRepository;
import com.charlottetattoo.api.repository.FlashRepository;
import com.charlottetattoo.api.repository.SlotRepository;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Regola anti doppia-prenotazione:
 * appena un cliente invia la richiesta, il flash e lo slot passano a PENDING (bloccati).
 * Nessun altro puo' sceglierli finche' Charlotte non accetta o rifiuta.
 *  - Accetta  -> flash UNAVAILABLE, slot BOOKED
 *  - Rifiuta / il cliente annulla -> flash AVAILABLE, slot FREE (tornano liberi)
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlashRepository flashRepository;
    private final SlotRepository slotRepository;
    private final int maxPendingBookings;

    public BookingService(BookingRepository bookingRepository, FlashRepository flashRepository,
                          SlotRepository slotRepository,
                          @Value("${app.max-pending-bookings}") int maxPendingBookings) {
        this.bookingRepository = bookingRepository;
        this.flashRepository = flashRepository;
        this.slotRepository = slotRepository;
        this.maxPendingBookings = maxPendingBookings;
    }

    @Transactional
    public Booking create(User user, BookingRequest request) {
        // Charlotte riceve le richieste, non le fa
        if (user.getRole() == Role.ADMIN) {
            throw ApiException.forbidden("Charlotte non può prenotare: gestisce le richieste dal pannello");
        }

        // Limite di richieste in attesa per cliente, cosi' nessuno blocca tutta la galleria
        long pending = bookingRepository.countByUserAndStatus(user, BookingStatus.PENDING);
        if (pending >= maxPendingBookings) {
            throw ApiException.conflict("Hai già " + maxPendingBookings
                    + " richieste in attesa: aspetta la risposta di Charlotte prima di farne altre");
        }

        // Blocchiamo flash e slot nel database: se due persone cliccano insieme,
        // la seconda aspetta la fine della prima e poi trova gia' tutto occupato.
        Flash flash = flashRepository.findByIdForUpdate(request.flashId())
                .orElseThrow(() -> ApiException.notFound("Flash non trovato"));
        AvailabilitySlot slot = slotRepository.findByIdForUpdate(request.slotId())
                .orElseThrow(() -> ApiException.notFound("Data o orario non trovati"));

        if (flash.getStatus() == FlashStatus.PENDING) {
            throw ApiException.conflict(
                    "Questo flash è già stato richiesto da un'altra persona ed è in attesa di conferma");
        }
        if (flash.getStatus() == FlashStatus.UNAVAILABLE) {
            throw ApiException.conflict("Questo flash non è più disponibile");
        }
        if (slot.getStatus() != SlotStatus.FREE) {
            throw ApiException.conflict("Questa giornata è appena stata prenotata, scegline un'altra");
        }
        if (SlotService.isInPast(slot)) {
            throw ApiException.badRequest("Non puoi prenotare una data già passata");
        }

        flash.setStatus(FlashStatus.PENDING);
        slot.setStatus(SlotStatus.PENDING);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setFlash(flash);
        booking.setSlot(slot);
        booking.setNote(request.note() == null || request.note().isBlank() ? null : request.note().trim());
        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<Booking> findMine(User user) {
        return bookingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /** Per Charlotte: tutte le richieste, oppure solo quelle con un certo stato. */
    public List<Booking> findAll(BookingStatus status) {
        if (status == null) {
            return bookingRepository.findAllByOrderByCreatedAtDesc();
        }
        return bookingRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /** Charlotte accetta e comunica l'orario preciso, che deve essere dentro la fascia scelta dal cliente. */
    @Transactional
    public Booking accept(Long id, LocalTime time) {
        Booking booking = getById(id);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw ApiException.conflict("Questa richiesta è già stata gestita");
        }
        DayPeriod period = booking.getSlot().getPeriod();
        if (!period.contains(time)) {
            throw ApiException.badRequest("Il cliente ha scelto " + period.labelWithArticle() + ": scegli un orario tra le "
                    + period.getStart() + " e le " + period.getEnd());
        }
        Flash flash = lockFlash(booking);
        AvailabilitySlot slot = lockSlot(booking);

        flash.setStatus(FlashStatus.UNAVAILABLE);
        slot.setStatus(SlotStatus.BOOKED);
        booking.setStatus(BookingStatus.ACCEPTED);
        booking.setConfirmedTime(time);
        return booking;
    }

    @Transactional
    public Booking reject(Long id, String reason) {
        Booking booking = getById(id);
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw ApiException.conflict("Questa richiesta è già stata gestita");
        }
        releaseFlashAndSlot(booking);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setConfirmedTime(null);
        booking.setRejectionReason(reason.trim());
        return booking;
    }

    @Transactional
    public Booking cancel(User user, Long id) {
        Booking booking = getById(id);
        // Un cliente puo' annullare solo le proprie richieste
        if (!booking.getUser().getId().equals(user.getId())) {
            throw ApiException.notFound("Richiesta non trovata");
        }
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.ACCEPTED) {
            throw ApiException.conflict("Questa richiesta non si può più annullare");
        }
        releaseFlashAndSlot(booking);
        booking.setStatus(BookingStatus.CANCELLED);
        return booking;
    }

    private Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Richiesta non trovata"));
    }

    // Ordine fisso (prima flash, poi slot) per evitare blocchi incrociati nel database
    private void releaseFlashAndSlot(Booking booking) {
        lockFlash(booking).setStatus(FlashStatus.AVAILABLE);
        lockSlot(booking).setStatus(SlotStatus.FREE);
    }

    private Flash lockFlash(Booking booking) {
        return flashRepository.findByIdForUpdate(booking.getFlash().getId())
                .orElseThrow(() -> ApiException.notFound("Flash non trovato"));
    }

    private AvailabilitySlot lockSlot(Booking booking) {
        return slotRepository.findByIdForUpdate(booking.getSlot().getId())
                .orElseThrow(() -> ApiException.notFound("Data o orario non trovati"));
    }
}
