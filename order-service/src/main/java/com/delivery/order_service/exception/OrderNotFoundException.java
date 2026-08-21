package com.delivery.order_service.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Pedido Não Encontrado");
    }
}
