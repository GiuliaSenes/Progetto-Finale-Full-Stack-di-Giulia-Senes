package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.CreateSlotsRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.DayPeriod;
import com.charlottetattoo.api.model.SlotStatus;
import com.charlottetattoo.api.repository.BookingRepository;
import com.charlottetattoo.api.repository.SlotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotService {

    // Prima per data, poi mattina prima del pomeriggio
    private static final Comparator<AvailabilitySlot> BY_DATE_AND_PERIOD =
            Comparator.comparing(AvailabilitySlot::getDate).thenComparing(AvailabilitySlot::getPeriod);

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    public SlotService(SlotRepository slotRepository, BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    /** Slot liberi e non ancora passati: sono quelli che il cliente può scegliere. */
    public List<AvailabilitySlot> findFreeFutureSlots() {
        return slotRepository.findByStatusAndDateGreaterThanEqual(SlotStatus.FREE, LocalDate.now())
                .stream()
                .filter(slot -> !isInPast(slot))
                .sorted(BY_DATE_AND_PERIOD)
                .toList();
    }

    /** Tutti gli slot da oggi in poi (per il pannello di Charlotte). */
    public List<AvailabilitySlot> findAllFromToday() {
        return slotRepository.findByDateGreaterThanEqual(LocalDate.now())
                .stream()
                .sorted(BY_DATE_AND_PERIOD)
                .toList();
    }

    public List<AvailabilitySlot> createSlots(CreateSlotsRequest request) {
        if (request.date().isBefore(LocalDate.now())) {
            throw ApiException.badRequest("Non puoi aggiungere disponibilità in una data passata");
        }

        List<AvailabilitySlot> created = new ArrayList<>();
        for (DayPeriod period : request.periods()) {
            // Se lo slot esiste già lo saltiamo
            if (slotRepository.existsByDateAndPeriod(request.date(), period)) {
                continue;
            }
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setDate(request.date());
            slot.setPeriod(period);
            slot.setStatus(SlotStatus.FREE);
            created.add(slotRepository.save(slot));
        }
        return created;
    }

    @Transactional
    public void delete(Long id) {
        AvailabilitySlot slot = slotRepository.findByIdForUpdate(id)
                .orElseThrow(() -> ApiException.notFound("Disponibilità non trovata"));

        if (slot.getStatus() != SlotStatus.FREE) {
            throw ApiException.conflict("Questa disponibilità ha una richiesta collegata: non si può eliminare");
        }
        // Vecchie richieste rifiutate/annullate su questo slot: le togliamo insieme allo slot
        bookingRepository.deleteBySlotId(id);
        slotRepository.delete(slot);
    }

    /** Uno slot è passato quando la sua mezza giornata è finita. */
    public static boolean isInPast(AvailabilitySlot slot) {
        return LocalDateTime.of(slot.getDate(), slot.getPeriod().getEnd()).isBefore(LocalDateTime.now());
    }
}
