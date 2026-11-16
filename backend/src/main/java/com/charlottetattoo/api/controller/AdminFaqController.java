package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.FaqRequest;
import com.charlottetattoo.api.dto.FaqResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.service.FaqService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Charlotte aggiunge, modifica ed elimina le FAQ: solo ADMIN. */
@RestController
@RequestMapping("/api/admin/faqs")
public class AdminFaqController {

    private final FaqService faqService;

    public AdminFaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaqResponse create(@Valid @RequestBody FaqRequest request) {
        return Mapper.toFaq(faqService.create(request));
    }

    @PutMapping("/{id}")
    public FaqResponse update(@PathVariable Long id, @Valid @RequestBody FaqRequest request) {
        return Mapper.toFaq(faqService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
