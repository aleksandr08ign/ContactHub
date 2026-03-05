package com.example.ContactHub.exception;

/**
 * Исключение, выбрасываемое при неверном запросе от клиента.
 * Например, когда передан некорректный ID, отсутствуют обязательные поля
 * Приводит к ответу с HTTP статусом 400 Bad Request.
 */
public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
    }
}
