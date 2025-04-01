package com.capricon.Collab_Project.exception;

import org.springframework.http.HttpStatus;

public class TechnicalException extends BaseException {
    public TechnicalException(String message, HttpStatus status) {
        super(message, status);
    }
}
