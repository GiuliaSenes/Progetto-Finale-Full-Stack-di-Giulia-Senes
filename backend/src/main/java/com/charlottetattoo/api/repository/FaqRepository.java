package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.Faq;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    // Le FAQ restano nell'ordine in cui sono state create
    List<Faq> findAllByOrderByIdAsc();
}
