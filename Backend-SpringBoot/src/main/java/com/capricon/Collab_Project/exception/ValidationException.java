package com.capricon.Collab_Project.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {
    public ValidationException(String message, HttpStatus status) {
        super(message, status);
    }
}
