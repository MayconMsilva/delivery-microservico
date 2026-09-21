package com.delivery.delivery_service.exception;

public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException() {
        super("Entrega Não Encontrada");
    }
}
