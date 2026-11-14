package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.CreateSlotsRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.SlotStatus;
import com.charlottetattoo.api.repository.BookingRepository;
import com.charlottetattoo.api.repository.SlotRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SlotService {

    // Le uniche fasce possibili: ogni tatuaggio flash dura al massimo 2 ore
    public static final List<LocalTime> ALLOWED_START_TIMES = List.of(
            LocalTime.of(10, 0), LocalTime.of(12, 0), LocalTime.of(15, 0), LocalTime.of(17, 0));
    public static final int SLOT_HOURS = 2;

    private final SlotRepository slotRepository;
    private final BookingRepository bookingRepository;

    public SlotService(SlotRepository slotRepository, BookingRepository bookingRepository) {
        this.slotRepository = slotRepository;
        this.bookingRepository = bookingRepository;
    }

    /** Slot liberi e ancora nel futuro: sono quelli che il cliente puo' scegliere. */
    public List<AvailabilitySlot> findFreeFutureSlots() {
        return slotRepository
                .findByStatusAndDateGreaterThanEqualOrderByDateAscStartTimeAsc(SlotStatus.FREE, LocalDate.now())
                .stream()
                .filter(slot -> !isInPast(slot))
                .toList();
    }

    /** Tutti gli slot da oggi in poi (per il pannello di Charlotte). */
    public List<AvailabilitySlot> findAllFromToday() {
        return slotRepository.findByDateGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate.now());
    }

    public List<AvailabilitySlot> createSlots(CreateSlotsRequest request) {
        if (request.date().isBefore(LocalDate.now())) {
            throw ApiException.badRequest("Non puoi aggiungere disponibilità in una data passata");
        }

        List<AvailabilitySlot> created = new ArrayList<>();
        for (LocalTime start : request.startTimes()) {
            if (!ALLOWED_START_TIMES.contains(start)) {
                throw ApiException.badRequest("Fascia oraria non valida: " + start);
            }
            // Se lo slot esiste gia' lo saltiamo
            if (slotRepository.existsByDateAndStartTime(request.date(), start)) {
                continue;
            }
            AvailabilitySlot slot = new AvailabilitySlot();
            slot.setDate(request.date());
            slot.setStartTime(start);
            slot.setEndTime(start.plusHours(SLOT_HOURS));
            slot.setStatus(SlotStatus.FREE);
            created.add(slotRepository.save(slot));
        }
        return created;
    }

    @Transactional
    public void delete(Long id) {
        AvailabilitySlot slot = slotRepository.findByIdForUpdate(id)
                .orElseThrow(() -> ApiException.notFound("Fascia oraria non trovata"));

        if (slot.getStatus() != SlotStatus.FREE) {
            throw ApiException.conflict("Questa fascia ha una richiesta collegata: non si può eliminare");
        }
        // Vecchie richieste rifiutate/annullate su questo slot: le togliamo insieme allo slot
        bookingRepository.deleteBySlotId(id);
        slotRepository.delete(slot);
    }

    public static boolean isInPast(AvailabilitySlot slot) {
        return LocalDateTime.of(slot.getDate(), slot.getStartTime()).isBefore(LocalDateTime.now());
    }
}
