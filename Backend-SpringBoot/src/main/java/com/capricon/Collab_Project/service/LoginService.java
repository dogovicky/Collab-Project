package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.LoginRequest;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.dto.UserDTOResponse;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.rmi.ServerException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
public class LoginService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public LoginService(UserRepo userRepo, JwtService jwtService, AuthenticationManager authManager) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authManager = authManager;
    }

    public CompletableFuture<UserDTOResponse> loginUser(LoginRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                User user = userRepo.findByUsername(request.getUsername())
                        .orElseThrow(() -> new UserException("User does not exist"));

                if (!user.getIsEnabled()) {
                    throw new ValidationException("Account not yet verified");
                }

                Authentication auth = authManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername() , request.getPassword())
                );

                if (auth.isAuthenticated()) {
                    String token = jwtService.generateToken(request.getUsername());

                    UserDTO userDTO = UserDTO.builder()
                            .username(user.getUsername())
                            .fullName(user.getFullName())
                            .email(user.getEmail())
                            .build();

                    return new UserDTOResponse(userDTO, token);
                } else {
                    throw new ValidationException("Authentication Failed");
                }
            } catch (BadCredentialsException ex) {
                throw new ValidationException("Invalid username or password");
            } catch (DisabledException ex) {
                throw new UserException("Account is disabled");
            }

        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();
            throw (cause instanceof ValidationException
                    || cause instanceof UserException
                    || cause instanceof BusinessException)
                    ? new CompletionException(cause)
                    : new CompletionException(new ServerException("Account verification failed"));
        });
    }

}
