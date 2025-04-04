package com.capricon.Collab_Project.exception;

import com.capricon.Collab_Project.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericBaseException(BaseException ex, HttpServletRequest request) {

        log.error("Unhandled exception occurred: ", ex);
        ApiResponse<Object> response = ApiResponse.error(ex.getStatus(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        return handleGenericBaseException(ex, request);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<ApiResponse<Object>> handleTechnicalException(TechnicalException ex, HttpServletRequest request) {
        return handleGenericBaseException(ex, request);
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserException(UserException ex, HttpServletRequest request) {
        return handleGenericBaseException(ex, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(ValidationException ex, HttpServletRequest request) {
        return handleGenericBaseException(ex, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotValidException(MethodArgumentNotValidException ex) {

        List<ApiResponse.ErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ApiResponse.ErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .code(fieldError.getCode()) // e.g., "NotBlank", "Size"
                        .build()
                )
                .toList();

        ApiResponse<Object> response = ApiResponse.error(
                HttpStatus.BAD_REQUEST,
                "Validation Error",
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {

        List<ApiResponse.ErrorDetail> errorDetails = ex.getConstraintViolations()
                .stream()
                .map(error -> ApiResponse.ErrorDetail.builder()
                        .message(error.getMessage())
                        .build()).toList();

        ApiResponse<Object> response = ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), errorDetails);
        return ResponseEntity.badRequest().body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        ApiResponse<Object> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
