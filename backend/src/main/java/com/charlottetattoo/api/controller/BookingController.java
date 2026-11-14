package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.BookingRequest;
import com.charlottetattoo.api.dto.BookingResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.service.BookingService;
import com.charlottetattoo.api.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/** Richieste di prenotazione del cliente loggato. */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    public BookingController(BookingService bookingService, UserService userService) {
        this.bookingService = bookingService;
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(Authentication authentication, @Valid @RequestBody BookingRequest request) {
        User user = userService.getByEmail(authentication.getName());
        return Mapper.toBooking(bookingService.create(user, request));
    }

    @GetMapping("/mine")
    public List<BookingResponse> mine(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return bookingService.findMine(user).stream().map(Mapper::toBooking).toList();
    }

    @PutMapping("/{id}/cancel")
    public BookingResponse cancel(Authentication authentication, @PathVariable Long id) {
        User user = userService.getByEmail(authentication.getName());
        return Mapper.toBooking(bookingService.cancel(user, id));
    }
}
