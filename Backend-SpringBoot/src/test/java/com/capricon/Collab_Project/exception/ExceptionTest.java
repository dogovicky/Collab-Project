package com.capricon.Collab_Project.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {

    @Test
    void shouldThrowBusinessExceptionWithCorrectMessage() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> {
                    throw new BusinessException("Business rule violated", HttpStatus.BAD_REQUEST);
                }
        );

        assertEquals("Business rule violated", exception.getMessage());
    }

    @Test
    void shouldThrowTechnicalExceptionWithCorrectMessage() {
        TechnicalException exception = assertThrows(
                TechnicalException.class,
                () -> {
                    throw new TechnicalException("System failure occurred", HttpStatus.INTERNAL_SERVER_ERROR);
                }
        );

        assertEquals("System failure occurred", exception.getMessage());
    }

    @Test
    void shouldThrowUserExceptionWithCorrectMessage() {
        UserException exception = assertThrows(
                UserException.class,
                () -> {
                    throw new UserException("User exception thrown", HttpStatus.UNAUTHORIZED);
                }
        );

        assertEquals("User exception thrown", exception.getMessage());
    }

}
