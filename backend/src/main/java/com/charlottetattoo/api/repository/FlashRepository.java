package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.Flash;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlashRepository extends JpaRepository<Flash, Long> {

    List<Flash> findAllByOrderByIdDesc();

    // Legge il flash e lo "blocca" fino alla fine della transazione:
    // cosi' due persone che cliccano insieme non possono prenotarlo entrambe.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select f from Flash f where f.id = :id")
    Optional<Flash> findByIdForUpdate(@Param("id") Long id);
}
