package com.capricon.Collab_Project.controller;

import com.capricon.Collab_Project.dto.AuthResponse;
import com.capricon.Collab_Project.dto.LoginRequest;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.dto.ValidationRequest;
import com.capricon.Collab_Project.service.LoginService;
import com.capricon.Collab_Project.service.PasswordResetService;
import com.capricon.Collab_Project.service.SignUpService;
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

    //private final AuthService authService;
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final PasswordResetService resetService;

    public AuthController(SignUpService signUpService, LoginService loginService, PasswordResetService resetService) {
        this.signUpService = signUpService;
        this.loginService = loginService;
        this.resetService = resetService;
    }

    @PostMapping("/signup")
    public CompletableFuture<String> signUp(@Valid @RequestBody UserDTO userDTO) {
        return signUpService.signUp(userDTO);
    }

    @PostMapping("/verify-account")
    public CompletableFuture<ResponseEntity<AuthResponse>> verifyAccount(@Valid @RequestBody ValidationRequest request) {
        return signUpService.verifyAccountByCode(request).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return loginService.loginUser(request).thenApply(ResponseEntity::ok);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> requestResetPasswordLink(@RequestBody Map<String, String> request) {
        resetService.requestPasswordReset(request.get("email"));
        return ResponseEntity.ok("Check your email for a reset link.");
    }

    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        resetService.resetPassword(request.get("token"), request.get("newPassword"));
        return ResponseEntity.ok("Password reset successful.");
    }

}
