package com.simonjoz.vetclinic.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnavailableDateException.class)
    public ResponseEntity<String> handleAppointmentDateUnavailable(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<String> handleInvalidPin(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RemovalFailureException.class)
    public ResponseEntity<String> handleRemovalFailure(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            HttpServletRequest request, ConstraintViolationException ex) {

        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();

        List<String> errorMessagesList = constraintViolations.stream()
                .map(ConstraintViolation::getMessageTemplate)
                .collect(Collectors.toList());

        Map<String, Object> responseBody = createResponseMap(request, errorMessagesList);
        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(
            HttpServletRequest request, MethodArgumentNotValidException ex) {

        List<String> errorsList = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        Map<String, Object> responseBody = createResponseMap(request, errorsList);

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleMethodNotReadable(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(ex.getCause().getMessage(),
                ex.getHttpInputMessage().getHeaders(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, Object> createResponseMap(HttpServletRequest request, List<String> errorsList) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", new Date());
        responseBody.put("status", HttpServletResponse.SC_BAD_REQUEST);
        responseBody.put("message", "");
        responseBody.put("error", errorsList);
        responseBody.put("path", request.getRequestURI());
        return responseBody;
    }


}
