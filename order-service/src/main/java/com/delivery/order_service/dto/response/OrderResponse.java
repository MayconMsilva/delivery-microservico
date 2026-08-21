package com.delivery.order_service.dto.response;

import com.delivery.order_service.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderResponse(


        Long id,
        OrderStatus status,
        BigDecimal total,
        Instant createdAt
) {
}
