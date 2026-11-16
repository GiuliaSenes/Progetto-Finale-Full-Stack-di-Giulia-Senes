package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.FaqRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.Faq;
import com.charlottetattoo.api.repository.FaqRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> findAll() {
        return faqRepository.findAllByOrderByIdAsc();
    }

    public Faq create(FaqRequest request) {
        Faq faq = new Faq();
        faq.setQuestion(request.question().trim());
        faq.setAnswer(request.answer().trim());
        return faqRepository.save(faq);
    }

    public Faq update(Long id, FaqRequest request) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Domanda non trovata"));
        faq.setQuestion(request.question().trim());
        faq.setAnswer(request.answer().trim());
        return faqRepository.save(faq);
    }

    public void delete(Long id) {
        if (!faqRepository.existsById(id)) {
            throw ApiException.notFound("Domanda non trovata");
        }
        faqRepository.deleteById(id);
    }
}
