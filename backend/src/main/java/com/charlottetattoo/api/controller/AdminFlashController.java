package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.FlashResponse;
import com.charlottetattoo.api.dto.FlashUpdateRequest;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.service.FlashService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Gestione dei flash: solo ADMIN (vedi SecurityConfig). */
@RestController
@RequestMapping("/api/admin/flashes")
public class AdminFlashController {

    private final FlashService flashService;

    public AdminFlashController(FlashService flashService) {
        this.flashService = flashService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public FlashResponse create(@RequestParam("image") MultipartFile image,
                                @RequestParam("size") String size) {
        return Mapper.toFlash(flashService.create(image, size));
    }

    @PutMapping("/{id}")
    public FlashResponse update(@PathVariable Long id, @Valid @RequestBody FlashUpdateRequest request) {
        return Mapper.toFlash(flashService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        flashService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
