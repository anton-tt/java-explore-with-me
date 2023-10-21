package ru.practicum.server.exception;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class ErrorMapper {

    public ErrorResponse toErrorResponse(String status, String reason, String message, LocalDateTime timestamp) {
        return ErrorResponse.builder()
                .status(status)
                .reason(reason)
                .message(message)
                .timestamp(timestamp)
                .build();
    }

}