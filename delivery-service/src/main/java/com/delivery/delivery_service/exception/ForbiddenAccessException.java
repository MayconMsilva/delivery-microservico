package com.delivery.delivery_service.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException() {
        super("Você Não Tem Permissão para Acessar este Recurso");
    }
}
