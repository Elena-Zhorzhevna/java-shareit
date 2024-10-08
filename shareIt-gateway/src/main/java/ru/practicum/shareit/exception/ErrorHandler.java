package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Обработчик ошибок.
 * Класс перехватывает исключения, выбрасываемые в контроллерах.
 * Возвращает соответствующие HTTP-статусы и сообщения об ошибках.
 */
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({InvalidStateException.class, InvalidRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidRequest(RuntimeException e) {
        return new ErrorResponse("Некорректный тип запроса", e.getMessage());
    }
}