package com.capricon.Collab_Project.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTest {

    @Test
    void shouldThrowBusinessExceptionWithCorrectMessage() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> {
                    throw new BusinessException("Business rule violated");
                }
        );

        assertEquals("Business rule violated", exception.getMessage());
    }

    @Test
    void shouldThrowTechnicalExceptionWithCorrectMessage() {
        TechnicalException exception = assertThrows(
                TechnicalException.class,
                () -> {
                    throw new TechnicalException("System failure occurred");
                }
        );

        assertEquals("System failure occurred", exception.getMessage());
    }

}
