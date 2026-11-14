package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.ConsultationResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.service.ConsultationService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Consulenze ricevute: solo ADMIN. */
@RestController
@RequestMapping("/api/admin/consultations")
public class AdminConsultationController {

    private final ConsultationService consultationService;

    public AdminConsultationController(ConsultationService consultationService) {
        this.consultationService = consultationService;
    }

    @GetMapping
    public List<ConsultationResponse> list() {
        return consultationService.findAll().stream().map(Mapper::toConsultation).toList();
    }

    @PutMapping("/{id}/handled")
    public ConsultationResponse markHandled(@PathVariable Long id) {
        return Mapper.toConsultation(consultationService.markHandled(id));
    }
}
