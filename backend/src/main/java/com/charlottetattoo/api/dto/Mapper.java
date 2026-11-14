package com.charlottetattoo.api.dto;

import com.charlottetattoo.api.model.AvailabilitySlot;
import com.charlottetattoo.api.model.Booking;
import com.charlottetattoo.api.model.Consultation;
import com.charlottetattoo.api.model.Flash;
import com.charlottetattoo.api.model.User;

/** Trasforma le entita' del database nei JSON che mandiamo al frontend. */
public class Mapper {

    private Mapper() {
    }

    public static String imageUrl(String filename) {
        return filename == null ? null : "/uploads/" + filename;
    }

    public static UserResponse toUser(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPhone(), user.getRole().name());
    }

    public static FlashResponse toFlash(Flash flash) {
        return new FlashResponse(flash.getId(), imageUrl(flash.getImageFile()),
                flash.getSize(), flash.getStatus().name());
    }

    public static SlotResponse toSlot(AvailabilitySlot slot) {
        return new SlotResponse(slot.getId(), slot.getDate(), slot.getPeriod().name(),
                slot.getStatus().name());
    }

    public static BookingResponse toBooking(Booking booking) {
        return new BookingResponse(booking.getId(), toFlash(booking.getFlash()),
                toSlot(booking.getSlot()), toUser(booking.getUser()), booking.getNote(),
                booking.getStatus().name(), booking.getConfirmedTime(), booking.getRejectionReason(),
                booking.getCreatedAt());
    }

    public static ConsultationResponse toConsultation(Consultation c) {
        return new ConsultationResponse(c.getId(), toUser(c.getUser()), c.getMessage(),
                imageUrl(c.getReferenceImageFile()), c.getStatus().name(), c.getCreatedAt());
    }
}
