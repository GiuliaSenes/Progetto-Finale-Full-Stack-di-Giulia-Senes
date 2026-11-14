package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.ConsultationResponse;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.service.ConsultationService;
import com.charlottetattoo.api.service.UserService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Richieste di consulenza del cliente loggato. */
@RestController
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final UserService userService;

    public ConsultationController(ConsultationService consultationService, UserService userService) {
        this.consultationService = consultationService;
        this.userService = userService;
    }

    // multipart/form-data perché può contenere un'immagine
    @PostMapping(consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultationResponse create(Authentication authentication,
                                       @RequestParam("message") String message,
                                       @RequestParam(value = "image", required = false) MultipartFile image) {
        User user = userService.getByEmail(authentication.getName());
        return Mapper.toConsultation(consultationService.create(user, message, image));
    }

    @GetMapping("/mine")
    public List<ConsultationResponse> mine(Authentication authentication) {
        User user = userService.getByEmail(authentication.getName());
        return consultationService.findMine(user).stream().map(Mapper::toConsultation).toList();
    }
}
