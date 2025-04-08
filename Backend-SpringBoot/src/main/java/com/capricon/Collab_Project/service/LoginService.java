package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.ApiResponse;
import com.capricon.Collab_Project.dto.LoginRequest;
import com.capricon.Collab_Project.dto.LoginResponseDTO;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final Executor executor;

    public CompletableFuture<ApiResponse<LoginResponseDTO>> loginUser(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserException("User does not exist", HttpStatus.NOT_FOUND));

            if (!user.getIsEnabled()) {
                throw new UserException("Account not yet verified", HttpStatus.UNAUTHORIZED);
            }

            try {
                Authentication auth = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
                );

                String token = jwtService.generateToken(request.getUsername());
                LoginResponseDTO response = new LoginResponseDTO(token, buildUserDTO(user));
                return ApiResponse.success(response, "Login successful");

            } catch (BadCredentialsException e) {
                throw new UserException("Wrong password. Please try again.", HttpStatus.UNAUTHORIZED);
            }

        }, executor).exceptionally(ex -> {
            Throwable cause = (ex instanceof CompletionException) ? ex.getCause() : ex;
            log.error("Error logging in user {}:", request.getUsername());
            if (cause instanceof UserException userException) {
                return ApiResponse.error(userException.getStatus(), userException.getMessage());
            } else {
                return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
            }
        });
    }

    private UserDTO buildUserDTO(User user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }

}
