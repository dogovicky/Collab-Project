package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.LoginRequest;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.dto.UserDTOResponse;
import com.capricon.Collab_Project.dto.ValidationRequest;
import com.capricon.Collab_Project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public CompletableFuture<String> signUp(@Valid @RequestBody UserDTO userDTO) {
        return authService.signUp(userDTO);
    }

    @PostMapping("/verify-account")
    public CompletableFuture<UserDTOResponse> verifyAccount(@Valid @RequestBody ValidationRequest request) {
        return authService.verifyAccount(request);
    }

    @PostMapping("/login")
    public CompletableFuture<UserDTOResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.loginUser(request);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestResetPasswordLink(@RequestBody Map<String, String> request) {
        authService.requestPasswordReset(request.get("email"));
        return ResponseEntity.ok("Check your email for a reset link.");
    }

    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(request.get("token"), request.get("newPassword"));
        return ResponseEntity.ok("Password reset successful.");
    }

}
