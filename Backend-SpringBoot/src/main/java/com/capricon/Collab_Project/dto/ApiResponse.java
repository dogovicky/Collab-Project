package com.capricon.Collab_Project.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class ApiResponse<T> {

    // Nested class for detailed error information
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorDetail {
        private String field; // Field where error occurred
        private String message; // Error message
        private String code; // Error code
    }

    //Unique identifier for the response
    private String responseId;

    //Timestamp for the response
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private int status; //HTTP status code

    private boolean success; // Success flag

    private String message; // Main Response message

    private String errorMessage; // Detailed error message

    private T data; // Payload of the response

    private List<ErrorDetail> errors; // List of validation errors or additional error details

    // Static method to create a successful response
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message("Operation successful")
                .data(data)
                .build();
    }

    // Static method to create a successful response with custom message
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    // Static method to create an error response
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .errorMessage(message)
                .build();
    }

    // Static method to create an error response with validation errors
    public static <T> ApiResponse<T> error(HttpStatus status, String message, List<ErrorDetail> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status.value())
                .errorMessage(message)
                .errors(errors)
                .build();
    }

    public Boolean isSuccess() {
        return success;
    }

}
