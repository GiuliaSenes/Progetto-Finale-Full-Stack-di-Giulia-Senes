package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.AuthResponse;
import com.charlottetattoo.api.dto.ForgotPasswordRequest;
import com.charlottetattoo.api.dto.LoginRequest;
import com.charlottetattoo.api.dto.RegisterRequest;
import com.charlottetattoo.api.dto.ResetPasswordRequest;
import com.charlottetattoo.api.service.AuthService;
import com.charlottetattoo.api.service.PasswordResetService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }

    // Risponde sempre allo stesso modo, anche se l'email non esiste
    @PostMapping("/forgot-password")
    public Map<String, String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.requestReset(request.email());
        return Map.of("message", "Se l'email è registrata, riceverai un link per scegliere una nuova password.");
    }

    @PostMapping("/reset-password")
    public Map<String, String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return Map.of("message", "Password cambiata! Ora puoi accedere.");
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
