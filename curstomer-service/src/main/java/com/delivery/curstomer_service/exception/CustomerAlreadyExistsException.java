package com.delivery.curstomer_service.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException() {
        super("Usuário Já Possui Um Cliente Cadastro");
    }
}
