package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.ChangePasswordRequest;
import com.charlottetattoo.api.dto.UpdateProfileRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> ApiException.notFound("Utente non trovato"));
    }

    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = getByEmail(email);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setPhone(request.phone().trim());
        return userRepository.save(user);
    }

    public void changePassword(String email, ChangePasswordRequest request) {
        User user = getByEmail(email);
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw ApiException.badRequest("La password attuale non è corretta");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
