package com.delivery.curstomer_service.exception;

public class ForbiddenAccessException extends RuntimeException {
    public ForbiddenAccessException() {
        super("Você Não Tem Permissão Para Acessar este Recurso");
    }
}
