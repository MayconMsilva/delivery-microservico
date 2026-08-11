package com.delivery_beta.auth_service.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {
        super("Email já está em uso");
    }
}
