package ru.practicum.shareit.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.practicum.shareit.exceptions.EntityAlreadyExistException;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.exceptions.JsonUpdateFieldsException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyApprovedException;
import ru.practicum.shareit.exceptions.booking.BookingAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.booking.RentTimeConstraintException;
import ru.practicum.shareit.exceptions.booking.UnknownStateException;
import ru.practicum.shareit.exceptions.item.ItemNotAvailableException;
import ru.practicum.shareit.exceptions.item.UnregisteredBookingException;
import ru.practicum.shareit.exceptions.user.EmailAlreadyRegisteredException;
import ru.practicum.shareit.exceptions.user.ObjectOwnerException;
import ru.practicum.shareit.exceptions.user.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            JsonUpdateFieldsException.class,
            EntityAlreadyExistException.class,
            EmailAlreadyRegisteredException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleEntityExistException(
            RuntimeException e, WebRequest request) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(
                responseBody(HttpStatus.CONFLICT, e.getMessage(), request),
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            UserNotFoundException.class,
            ObjectOwnerException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundOrIncorrectPathVariable(
            RuntimeException e, WebRequest request) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(
                responseBody(HttpStatus.NOT_FOUND, e.getMessage(), request),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            BookingAlreadyApprovedException.class,
            BookingAlreadyRegisteredException.class,
            RentTimeConstraintException.class,
            UnregisteredBookingException.class,
            ItemNotAvailableException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException e, WebRequest request) {

        log.warn(e.getMessage());
        return new ResponseEntity<>(
                responseBody(HttpStatus.BAD_REQUEST, e.getMessage(), request),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        log.warn(ex.getMessage());
        return new ResponseEntity<>(
                responseBody(status, ex.getMessage(), request),
                status);
    }

    @ExceptionHandler(value = MethodNotAllowedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<Object> handleMethodNotAllowedException(
            final Throwable e, WebRequest request) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(
                responseBody(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), request),
                HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = UnknownStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnknownState(
            UnknownStateException e, WebRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleThrowable(
            final Throwable e, WebRequest request) {
        log.warn(e.getMessage());
        return new ResponseEntity<>(
                responseBody(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> responseBody(HttpStatus status, String error, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", error);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }
}
