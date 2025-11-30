package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.exception.dto.ExceptionDto;
import ru.practicum.ewm.exception.mapper.ExceptionMapper;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ExceptionMapper exceptionMapper;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionDto> handleThrowable(Throwable ex) {
        log.error("Unexpected error", ex);
        ExceptionDto body = exceptionMapper.mapExceptionParamsToDto(
                ex.getMessage(),
                "Unexpected error.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "%s: %s".formatted(error.getField(), error.getDefaultMessage()))
                .findFirst()
                .orElse("Validation error");
        log.warn("Ошибка валидации: {}", message);
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(
                message,
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("Некорректный запрос: {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(
                ex.getMessage(),
                "Incorrectly made request.",
                HttpStatus.BAD_REQUEST
        );
        return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("Ошибка при создании объекта в БД: {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(
                ex.getMessage(),
                "Integrity constraint has been violated.",
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleObjectNotFoundException(ObjectNotFoundException ex) {
        log.warn("Объект не найден - {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(ex.getMessage(), "The required object was not found.", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(exceptionDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnboundCategoriesException.class)
    public ResponseEntity<ExceptionDto> handleUnboundCategoriesException(UnboundCategoriesException ex) {
        log.warn("Попытка удаления категории которая привязана к событию / событиям - {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(ex.getMessage(), "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EventUpdateException.class)
    public ResponseEntity<ExceptionDto> handleEventUpdateException(EventUpdateException ex) {
        log.warn("Ошибка при обновлении события - {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(ex.getMessage(), "For the requested operation the conditions are not met.", HttpStatus.CONFLICT);
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RequestStatusUpdateException.class)
    public ResponseEntity<ExceptionDto> handleRequestStatusUpdateException(RequestStatusUpdateException ex) {
        log.warn("Ошибка при обновлении статуса заявок - {}", ex.getMessage());
        ExceptionDto exceptionDto = exceptionMapper.mapExceptionParamsToDto(
                ex.getMessage(),
                "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT
        );
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
    }
}
