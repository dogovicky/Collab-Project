package com.capricon.Collab_Project.exception;

import org.springframework.http.HttpStatus;

public class UserException extends BaseException {
    public UserException(String message, HttpStatus status) {
        super(message, status);
    }
}
