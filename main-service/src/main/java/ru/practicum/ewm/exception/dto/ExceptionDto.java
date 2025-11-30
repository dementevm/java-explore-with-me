package ru.practicum.ewm.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ExceptionDto {
    String message;
    String reason;
    String status;
    LocalDateTime timestamp;
}
