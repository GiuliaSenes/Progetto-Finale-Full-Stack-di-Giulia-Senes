package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.BookingResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.dto.RejectRequest;
import com.charlottetattoo.api.model.BookingStatus;
import com.charlottetattoo.api.service.BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Charlotte accetta o rifiuta le richieste: solo ADMIN. */
@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Filtro facoltativo: /api/admin/bookings?status=PENDING
    @GetMapping
    public List<BookingResponse> list(@RequestParam(required = false) BookingStatus status) {
        return bookingService.findAll(status).stream().map(Mapper::toBooking).toList();
    }

    @PutMapping("/{id}/accept")
    public BookingResponse accept(@PathVariable Long id) {
        return Mapper.toBooking(bookingService.accept(id));
    }

    @PutMapping("/{id}/reject")
    public BookingResponse reject(@PathVariable Long id, @Valid @RequestBody RejectRequest request) {
        return Mapper.toBooking(bookingService.reject(id, request.reason()));
    }
}
