package com.delivery.order_service.event;

import java.time.Instant;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        Instant createdAt
) {
}
