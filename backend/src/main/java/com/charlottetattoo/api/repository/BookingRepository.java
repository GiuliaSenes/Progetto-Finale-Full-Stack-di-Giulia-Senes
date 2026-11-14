package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.Booking;
import com.charlottetattoo.api.model.BookingStatus;
import com.charlottetattoo.api.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByCreatedAtDesc(User user);

    List<Booking> findAllByOrderByCreatedAtDesc();

    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);

    long countByUserAndStatus(User user, BookingStatus status);

    boolean existsByFlashIdAndStatusIn(Long flashId, Collection<BookingStatus> statuses);

    void deleteByFlashId(Long flashId);

    void deleteBySlotId(Long slotId);
}
