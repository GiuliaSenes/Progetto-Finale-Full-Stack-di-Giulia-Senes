package com.charlottetattoo.api.repository;

import com.charlottetattoo.api.model.Flash;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlashRepository extends JpaRepository<Flash, Long> {

    List<Flash> findAllByOrderByIdDesc();
}
