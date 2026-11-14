package com.charlottetattoo.api.dto;

public record AuthResponse(String token, UserResponse user) {
}
