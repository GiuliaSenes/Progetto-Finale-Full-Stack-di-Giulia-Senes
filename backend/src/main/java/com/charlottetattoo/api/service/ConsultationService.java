package com.charlottetattoo.api.service;

import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.Consultation;
import com.charlottetattoo.api.model.ConsultationStatus;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.ConsultationRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final FileStorageService fileStorageService;

    public ConsultationService(ConsultationRepository consultationRepository,
                               FileStorageService fileStorageService) {
        this.consultationRepository = consultationRepository;
        this.fileStorageService = fileStorageService;
    }

    public Consultation create(User user, String message, MultipartFile referenceImage) {
        if (message == null || message.isBlank()) {
            throw ApiException.badRequest("Descrivi la tua idea di tatuaggio");
        }
        if (message.length() > 1500) {
            throw ApiException.badRequest("Il messaggio può avere al massimo 1500 caratteri");
        }

        Consultation consultation = new Consultation();
        consultation.setUser(user);
        consultation.setMessage(message.trim());
        // L'immagine di riferimento e' facoltativa
        if (referenceImage != null && !referenceImage.isEmpty()) {
            consultation.setReferenceImageFile(fileStorageService.saveImage(referenceImage));
        }
        return consultationRepository.save(consultation);
    }

    public List<Consultation> findMine(User user) {
        return consultationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Consultation> findAll() {
        return consultationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Consultation markHandled(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Consulenza non trovata"));
        consultation.setStatus(ConsultationStatus.HANDLED);
        return consultationRepository.save(consultation);
    }
}
