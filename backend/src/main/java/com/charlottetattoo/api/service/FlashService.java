package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.FlashUpdateRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.BookingStatus;
import com.charlottetattoo.api.model.Flash;
import com.charlottetattoo.api.model.FlashStatus;
import com.charlottetattoo.api.repository.BookingRepository;
import com.charlottetattoo.api.repository.FlashRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FlashService {

    private final FlashRepository flashRepository;
    private final BookingRepository bookingRepository;
    private final FileStorageService fileStorageService;

    public FlashService(FlashRepository flashRepository, BookingRepository bookingRepository,
                        FileStorageService fileStorageService) {
        this.flashRepository = flashRepository;
        this.bookingRepository = bookingRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Flash> findAll() {
        return flashRepository.findAllByOrderByIdDesc();
    }

    public Flash getById(Long id) {
        return flashRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Flash non trovato"));
    }

    public Flash create(MultipartFile image, String size) {
        if (size == null || size.isBlank()) {
            throw ApiException.badRequest("Scrivi la dimensione del flash");
        }
        Flash flash = new Flash();
        flash.setImageFile(fileStorageService.saveImage(image));
        flash.setSize(size.trim());
        flash.setStatus(FlashStatus.AVAILABLE);
        return flashRepository.save(flash);
    }

    @Transactional
    public Flash update(Long id, FlashUpdateRequest request) {
        Flash flash = flashRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Flash non trovato"));

        if (request.status() == FlashStatus.PENDING) {
            throw ApiException.badRequest("Lo stato 'in attesa' si imposta da solo quando arriva una richiesta");
        }
        // Se c'e' una richiesta in attesa, prima va accettata o rifiutata
        if (flash.getStatus() == FlashStatus.PENDING) {
            throw ApiException.conflict("Questo flash ha una richiesta in attesa: accettala o rifiutala prima");
        }

        flash.setSize(request.size().trim());
        flash.setStatus(request.status());
        return flashRepository.save(flash);
    }

    @Transactional
    public void delete(Long id) {
        Flash flash = flashRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Flash non trovato"));

        boolean hasActiveBookings = bookingRepository.existsByFlashIdAndStatusIn(
                id, List.of(BookingStatus.PENDING, BookingStatus.ACCEPTED));
        if (hasActiveBookings) {
            throw ApiException.conflict(
                    "Questo flash ha una richiesta attiva: non si può eliminare. Puoi segnarlo come non disponibile.");
        }

        // Le vecchie richieste (rifiutate o annullate) si cancellano insieme al flash
        bookingRepository.deleteByFlashId(id);
        flashRepository.delete(flash);
        fileStorageService.delete(flash.getImageFile());
    }
}
