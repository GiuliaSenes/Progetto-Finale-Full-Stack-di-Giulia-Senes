package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.DayPeriod;
import com.charlottetattoo.api.model.SlotStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SlotRepository extends JpaRepository<AvailabilitySlot, Long> {

    List<AvailabilitySlot> findByDateGreaterThanEqual(LocalDate from);

    List<AvailabilitySlot> findByStatusAndDateGreaterThanEqual(SlotStatus status, LocalDate from);

    boolean existsByDateAndPeriod(LocalDate date, DayPeriod period);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AvailabilitySlot s where s.id = :id")
    Optional<AvailabilitySlot> findByIdForUpdate(@Param("id") Long id);
}
