package ru.practicum.server.exception;

public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }

}