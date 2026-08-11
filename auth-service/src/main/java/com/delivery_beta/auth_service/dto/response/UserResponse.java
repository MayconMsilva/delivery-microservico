package com.delivery_beta.auth_service.dto.response;

public record UserResponse(
        Long id,
        String email,
        String name
) {
}
