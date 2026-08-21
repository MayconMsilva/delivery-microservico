package com.delivery.order_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(

        @NotEmpty(message = "O Pedido Precisa ter ao menos um item")
        @Valid
        List<OrderItemRequest> items
) {
}
