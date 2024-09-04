package com.jwt.springsecurity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildResponseEntity(ErrorResponse errorResponse, HttpStatus status) {
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException ex) {
        return buildResponseEntity(new ErrorResponse("Already Exists", ex.getMessage()), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(NotAuthorizeException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizeException(NotAuthorizeException ex) {
        return buildResponseEntity(new ErrorResponse("Not Authorised", ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(InvalidTokenException ex) {
        return buildResponseEntity(new ErrorResponse("Invalid Token", ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return buildResponseEntity(new ErrorResponse("Not Found", ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return buildResponseEntity(new ErrorResponse("Internal Server Error", ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
