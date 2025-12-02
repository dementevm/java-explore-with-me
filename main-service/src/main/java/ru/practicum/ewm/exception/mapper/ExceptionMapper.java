package ru.practicum.ewm.exception.mapper;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.exception.dto.ExceptionDto;

import java.time.LocalDateTime;

@Component
public class ExceptionMapper {
    public ExceptionDto mapExceptionParamsToDto(String message, String reason, HttpStatus status) {
        LocalDateTime timestamp = LocalDateTime.now();
        return new ExceptionDto(message, reason, status.name(), timestamp);
    }
}
