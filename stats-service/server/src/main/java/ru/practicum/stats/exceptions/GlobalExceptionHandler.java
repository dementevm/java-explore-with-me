package ru.practicum.stats.exceptions;

import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        UUID uuid = UUID.randomUUID();
        if (stackTrace.length > 0) {
            StackTraceElement stackTraceElement = stackTrace[0];
            int lineNumber = stackTraceElement.getLineNumber();
            String fileName = stackTraceElement.getFileName();
            String className = stackTraceElement.getClassName();
            String methodName = stackTraceElement.getMethodName();
            log.warn("Произошла необработанная ошибка: id ошибки: {}, fileName: {}, className: {}, " +
                    "methodName: {}, lineNumber: {}, errorClass: {}, " +
                    "errorText: {}", uuid, fileName, className, methodName, lineNumber, ex.getClass(), ex.getMessage());
        } else {
            log.warn("Произошла необработанная ошибка - {}, id ошибки {}", ex.getMessage(), uuid);
        }
        error.put("error", "Произошла ошибка на сервере, id ошибки: " + uuid);

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidation(ValidationException e) {
        String error = e.getMessage();
        log.warn(error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        log.warn("Отсутствует обязательный параметр запроса: {}", e.getMessage());
        String error = e.getMessage();
        log.warn(error);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
