package com.delivery.delivery_service.dto;

import com.delivery.delivery_service.entity.enums.DeliveryStatus;

import java.time.Instant;

public record DeliveryResponse(
        Long orderId,
        DeliveryStatus status,
        Instant createdAt
) {
}
