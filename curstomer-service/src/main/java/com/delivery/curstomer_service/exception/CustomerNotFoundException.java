package com.delivery.curstomer_service.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException() {
        super("Cliente Não Encontrado");
    }
}
