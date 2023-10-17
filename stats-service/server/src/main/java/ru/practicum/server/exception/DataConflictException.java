package ru.practicum.server.exception;

public class DataConflictException extends RuntimeException {

    public DataConflictException(final String message) {
        super(message);
    }

}