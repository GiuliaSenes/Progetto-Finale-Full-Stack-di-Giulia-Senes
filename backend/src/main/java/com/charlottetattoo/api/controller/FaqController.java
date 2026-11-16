package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.FaqResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.service.FaqService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Elenco delle FAQ: pubblico, visibile anche a chi non ha un account. */
@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    private final FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @GetMapping
    public List<FaqResponse> list() {
        return faqService.findAll().stream().map(Mapper::toFaq).toList();
    }
}
