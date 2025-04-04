package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.ProfileRequest;
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
    public ResponseEntity<ApiResponse<ProfileRequest>> getProfileData(@RequestParam String username) {
        log.info("Received profile request for user: {}", username);
        try {
            ApiResponse<ProfileRequest> response = userService.getProfileData(username);
            if (response.isSuccess()) {
                log.info("Successfully retrieved profile data for user: {}", username);
                return ResponseEntity.ok(response);
            } else {
                log.error("Failed to retrieve profile data for user: {}", username);
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleProfileControllerException(ex);
        }
    }

    @PutMapping("/profile/update")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        try {
            ApiResponse<UserProfileDTO> response = userService.updateProfile(request);
            if (response.isSuccess()) {
                log.info("Successfully updated profile for user: {}", request.getUsername());
                return ResponseEntity.ok(response);
            } else {
                log.error("Failed to update profile for user: {}", request.getUsername());
                return ResponseEntity.status(response.getStatus()).body(response);
            }
        } catch (Exception ex) {
            return handleProfileControllerException(ex);
        }
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
