package com.jwt.springsecurity.exception;

public class NotAuthorizeException extends RuntimeException {
    public NotAuthorizeException(String message) {
        super(message);
    }

    public NotAuthorizeException(String message, Throwable cause) {
        super(message, cause);
    }

}
