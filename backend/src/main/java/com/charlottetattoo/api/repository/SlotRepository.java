package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.DayPeriod;
import com.charlottetattoo.api.model.SlotStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByDateGreaterThanEqual(LocalDate from);

    List<AvailabilitySlot> findByStatusAndDateGreaterThanEqual(SlotStatus status, LocalDate from);

    boolean existsByDateAndPeriod(LocalDate date, DayPeriod period);
}
