package com.delivery.delivery_service.event;

import java.time.Instant;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        Instant createdAt
) {
}
