package ru.practicum.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(ValidationException e) {
        log.info("Ошибка 400: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException e) {
        log.info("Ошибка 400: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MissingServletRequestParameterException e) {
        log.info("Ошибка 400: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.BAD_REQUEST.toString(), "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptionException(NotFoundException e) {
        log.info("Ошибка 404: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.NOT_FOUND.toString(), "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictExceptionException(DataConflictException e) {
        log.info("Ошибка 409: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.CONFLICT.toString(), "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataConflictExceptionException(DataIntegrityViolationException e) {
        log.info("Ошибка 409: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.CONFLICT.toString(), "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(Throwable e) {
        log.info("Ошибка 500: {}", e.getMessage());
        return ErrorMapper.toErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An unexpected error occurred.",
                e.getMessage(), LocalDateTime.now());
    }

}