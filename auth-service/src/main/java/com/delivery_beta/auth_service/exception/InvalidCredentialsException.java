package com.delivery_beta.auth_service.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Email ou Senha Inválidos");
    }
}
