package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.dto.SlotResponse;
import com.charlottetattoo.api.service.SlotService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Disponibilità di Charlotte: solo le fasce libere e future. */
@RestController
@RequestMapping("/api/slots")
public class SlotController {

    private final SlotService slotService;

    public SlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotResponse> listFree() {
        return slotService.findFreeFutureSlots().stream().map(Mapper::toSlot).toList();
    }
}
