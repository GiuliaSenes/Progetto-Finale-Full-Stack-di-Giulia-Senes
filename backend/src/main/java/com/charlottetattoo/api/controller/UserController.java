package com.charlottetattoo.api.controller;

import com.charlottetattoo.api.dto.ChangePasswordRequest;
import com.charlottetattoo.api.dto.Mapper;
import com.charlottetattoo.api.dto.UpdateProfileRequest;
import com.charlottetattoo.api.dto.UserResponse;
import com.charlottetattoo.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Profilo dell'utente loggato. */
@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse me(Authentication authentication) {
        return Mapper.toUser(userService.getByEmail(authentication.getName()));
    }

    @PutMapping
    public UserResponse updateProfile(Authentication authentication,
                                      @Valid @RequestBody UpdateProfileRequest request) {
        return Mapper.toUser(userService.updateProfile(authentication.getName(), request));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(Authentication authentication,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
