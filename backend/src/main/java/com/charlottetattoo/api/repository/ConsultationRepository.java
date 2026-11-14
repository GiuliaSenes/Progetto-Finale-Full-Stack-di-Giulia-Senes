package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.Consultation;
import com.charlottetattoo.api.model.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    List<Consultation> findByUserOrderByCreatedAtDesc(User user);

    List<Consultation> findAllByOrderByCreatedAtDesc();
}
