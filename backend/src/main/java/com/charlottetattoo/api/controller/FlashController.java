package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.FlashResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.service.FlashService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Galleria dei flash: visibile solo agli utenti loggati. */
@RestController
@RequestMapping("/api/flashes")
public class FlashController {

    private final FlashService flashService;

    public FlashController(FlashService flashService) {
        this.flashService = flashService;
    }

    @GetMapping
    public List<FlashResponse> list() {
        return flashService.findAll().stream().map(Mapper::toFlash).toList();
    }

    @GetMapping("/{id}")
    public FlashResponse get(@PathVariable Long id) {
        return Mapper.toFlash(flashService.getById(id));
    }
}
