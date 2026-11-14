package com.charlottetattoo.api.service;

import com.charlottetattoo.api.dto.AuthResponse;
import com.charlottetattoo.api.dto.LoginRequest;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.dto.RegisterRequest;
import com.charlottetattoo.api.exception.ApiException;
import com.charlottetattoo.api.model.Role;
import com.charlottetattoo.api.model.User;
import com.charlottetattoo.api.repository.UserRepository;
import com.charlottetattoo.api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw ApiException.conflict("Esiste già un account con questa email");
        }

        User user = new User();
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setPhone(request.phone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER); // chi si registra e' sempre un cliente, mai admin
        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user), Mapper.toUser(user));
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        // Stesso messaggio sia per email sbagliata sia per password sbagliata
        User user = userRepository.findByEmail(email)
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Email o password non corretti"));

        return new AuthResponse(jwtService.generateToken(user), Mapper.toUser(user));
    }
}
