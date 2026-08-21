package com.delivery.order_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OrderItemRequest(
        @NotBlank
        String productName,
        @Positive
        Integer quantity,
        @Positive
        BigDecimal price
) {
}
