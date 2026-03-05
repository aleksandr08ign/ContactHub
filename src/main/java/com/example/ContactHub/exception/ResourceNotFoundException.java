package com.example.ContactHub.exception;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс (клиент или контакт)
 * не найден в базе данных.
 * Приводит к ответу с HTTP статусом 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
