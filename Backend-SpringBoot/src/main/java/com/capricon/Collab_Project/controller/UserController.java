package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.ProfileUpdateRequest;
import com.capricon.Collab_Project.dto.UserProfileDTO;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public CompletableFuture<ResponseEntity<UserProfileDTO>> getProfileData(@RequestParam String username) {
        log.info("Received profile request for user: {}", username);

        return userService.fetchProfileData(username)
                .thenApply(profileData -> {
                    log.info("Successfully retrieved profile for user: {}", username);
                    return ResponseEntity.ok(profileData);
                }).exceptionally(ex -> {
                    log.error("Failed to retrieve profile data for user: {}", username, ex);

                    if (ex.getCause() instanceof UserException) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
                    }
                });
    }

    @PutMapping("/profile/update")
    public CompletableFuture<ResponseEntity<UserProfileDTO>> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return userService.updateUserProfile(request).thenApply(ResponseEntity::ok);
    }

}
