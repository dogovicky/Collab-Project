package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.*;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.service.LoginService;
import com.capricon.Collab_Project.service.PasswordResetService;
import com.capricon.Collab_Project.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    //private final AuthService authService;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final PasswordResetService resetService;

    @PostMapping("/signup")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> signUp(@Valid @RequestBody UserDTO userDTO) {
        return signUpService.signUp(userDTO)
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleAuthControllerException);
    }

    @PostMapping("/verify-account")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> verifyAccount(@Valid @RequestBody ValidationRequest request) {
        return signUpService.verifyAccountByCode(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleAuthControllerException);
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> login(@Valid @RequestBody LoginRequest request) {
        return loginService.loginUser(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleAuthControllerException);
    }

    @PostMapping("/forgot-password")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> requestResetPasswordLink(@RequestBody Map<String, String> request) {
        return resetService.requestEmail(request.get("email"))
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleAuthControllerException);
    }

    public CompletableFuture<ResponseEntity<ApiResponse<String>>> resetPassword(@RequestBody Map<String, String> request) {
        return resetService.savePassword(request.get("token"), request.get("newPassword"))
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleAuthControllerException);
    }


    private <T> ResponseEntity<ApiResponse<T>> handleAuthControllerException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        // More specific handling based on exception type
        assert cause != null;
        return switch (cause) {
            case UserException userException -> ResponseEntity.status(userException.getStatus())
                    .body(ApiResponse.error(userException.getStatus(), userException.getMessage()));
            case BusinessException businessException -> ResponseEntity.status(businessException.getStatus())
                    .body(ApiResponse.error(businessException.getStatus(), businessException.getMessage()));
            case ValidationException validationException -> ResponseEntity.status(validationException.getStatus())
                    .body(ApiResponse.error(validationException.getStatus(), validationException.getMessage()));
            default -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED, "Error logging in user"));
        };
    }

}
