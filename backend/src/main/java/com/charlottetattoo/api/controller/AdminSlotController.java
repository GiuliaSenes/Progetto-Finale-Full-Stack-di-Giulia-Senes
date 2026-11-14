package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.CreateSlotsRequest;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.dto.SlotResponse;
import com.charlottetattoo.api.service.SlotService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Gestione della disponibilità: solo ADMIN. */
@RestController
@RequestMapping("/api/admin/slots")
public class AdminSlotController {

    private final SlotService slotService;

    public AdminSlotController(SlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping
    public List<SlotResponse> listAll() {
        return slotService.findAllFromToday().stream().map(Mapper::toSlot).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<SlotResponse> create(@Valid @RequestBody CreateSlotsRequest request) {
        return slotService.createSlots(request).stream().map(Mapper::toSlot).toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        slotService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
