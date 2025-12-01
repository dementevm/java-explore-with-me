package ru.practicum.ewm.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionDto {
    private String message;
    private String reason;
    private String status;
    private LocalDateTime timestamp;
}
