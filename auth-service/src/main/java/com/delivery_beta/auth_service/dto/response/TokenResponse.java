package com.delivery_beta.auth_service.dto.response;

public record TokenResponse(
        String token,
        long expiresin
) {
}
