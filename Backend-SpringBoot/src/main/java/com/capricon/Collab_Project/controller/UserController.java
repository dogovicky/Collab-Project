package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.ProfileUpdateRequest;
import com.capricon.Collab_Project.dto.UserProfileDTO;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public CompletableFuture<ResponseEntity<ApiResponse<UserProfileDTO>>> getProfileData(@RequestParam String username) {
        log.info("Received profile request for user: {}", username);
        log.info("SecurityContext: {}", SecurityContextHolder.getContext().getAuthentication());
        return userService.fetchProfileData(username)
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleProfileControllerException);
    }

    @PutMapping("/profile/update")
    public CompletableFuture<ResponseEntity<ApiResponse<UserProfileDTO>>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateUserProfile(request)
                .thenApply(ResponseEntity::ok)
                .exceptionally(this::handleProfileControllerException);
    }

    private <T> ResponseEntity<ApiResponse<T>> handleProfileControllerException(Throwable ex) {
        Throwable cause = (ex instanceof CompletionException && ex.getCause() != null) ? ex.getCause() : ex;

        log.error("Error processing request caused by {}", cause != null ? cause.getMessage() : "Unknown cause", cause);

        if (cause instanceof UserException userException) {
            return ResponseEntity.status(userException.getStatus())
                    .body(ApiResponse.error(userException.getStatus(), userException.getMessage()));
        } else if (cause instanceof IllegalArgumentException argumentException){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST, argumentException.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"));
        }
    }

}
