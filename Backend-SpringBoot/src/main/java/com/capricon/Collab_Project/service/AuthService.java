package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.LoginRequest;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.dto.UserDTOResponse;
import com.capricon.Collab_Project.dto.ValidationRequest;
import com.capricon.Collab_Project.exception.*;
import com.capricon.Collab_Project.model.PasswordResetToken;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.enums.Gender;
import com.capricon.Collab_Project.repository.PasswordResetTokenRepo;
import com.capricon.Collab_Project.repository.UserRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.ServerException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class AuthService {

    private final UserRepo userRepo;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Executor executor;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordResetTokenRepo resetTokenRepo;

    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailService mailService,
                       Executor executor, JwtService jwtService, Validator validator, AuthenticationManager authManager,
                       PasswordResetTokenRepo resetTokenRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.executor = executor;
        this.jwtService = jwtService;
        this.validator = validator;
        this.authManager = authManager;
        this.resetTokenRepo = resetTokenRepo;
    }

    private void validateRequest(UserDTO request) {
        Set<ConstraintViolation<UserDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }


    public CompletableFuture<String> signUp(UserDTO request) {
        return CompletableFuture.supplyAsync(() -> {
            validateRequest(request);
            return signUpRequest(request);
        }).exceptionally(ex -> {
            log.error("Error during sign up for user {}: {}", request.getUsername(), ex.getMessage());
            Throwable cause = ex.getCause();
            throw (cause instanceof ValidationException || cause instanceof UserException || cause instanceof BusinessException)
                    ? new CompletionException(cause)
                    : new BaseException("Sign up failed");
        });
    }


    @Transactional
    private String signUpRequest(UserDTO request) {
        Optional<User> existingUser = userRepo.findByUsernameOrEmail(request.getUsername(), request.getEmail());
        if (existingUser.isPresent()) {
            throw new UserException("User already exists");
        }

        String verificationCode = mailService.generateVerificationCode();

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender() != null ? Gender.valueOf(request.getGender()) : null)
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .institution(request.getInstitution() != null ? request.getInstitution() : null)
                .bio(request.getBio() != null ? request.getBio() : null)
                .fieldOfInterest(request.getFieldOfInterest() != null ? request.getFieldOfInterest() : new ArrayList<>())
                .isEnabled(false)
                .verificationCode(verificationCode)
                .build();

        try {
            mailService.sendVerificationCode(request.getEmail(), request.getFullName(), verificationCode);
        } catch (Exception ex) {
            log.error("Failed to send email verification to {}: {}", request.getEmail(), ex.getMessage());
            throw new BusinessException("Failed to send verification email.");
        }
        userRepo.save(user);
        log.info("User registered successfully: username = {}, email = {}", user.getUsername(), user.getEmail());
        return "Registration successful. Check your email for account verification code.";
    }

    @Transactional
    public CompletableFuture<UserDTOResponse> verifyAccount(ValidationRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepo.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserException("User does not exist"));

            if (!user.getVerificationCode().equals(request.getCode())) {
                throw new ValidationException("Invalid verification code");
            }

            user.setIsEnabled(true);
            user.setVerificationCode(null);
            userRepo.save(user);

            UserDTO userDTO = UserDTO.builder()
                    .fullName(user.getFullName())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .bio(user.getBio())
                    .dateOfBirth(user.getDateOfBirth())
                    .fieldOfInterest(user.getFieldOfInterest())
                    .gender(String.valueOf(user.getGender()))
                    .build();
            return new UserDTOResponse( userDTO, jwtService.generateToken(request.getUsername()));

        }).exceptionally(ex -> {
            Throwable cause = ex.getCause();
            log.error("Failed to verify account {}: {}", request.getUsername(), cause.getMessage());

            throw (cause instanceof ValidationException) ? new CompletionException(cause)
                    : new CompletionException(new ServerException("Account verification failed"));
        });

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


    public void requestPasswordReset(String email) {

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UserException("User not registered"));

        //Generate unique reset token
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(Duration.ofMinutes(15));

        //Save token in database
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);
        resetTokenRepo.save(resetToken);

        //Send reset email
        String resetLink = "http://localhost:5173/reset-password?token=" + token;
        mailService.sendResetPasswordLink(email, resetLink, String.valueOf(expiry));

    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = resetTokenRepo.findByToken(token)
                .orElseThrow(() -> new ValidationException("Invalid token"));

        if (resetToken.isExpired()) {
            throw new ValidationException("Reset token has expired, try re-sending another request.");
        }

        //Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        resetTokenRepo.delete(resetToken);

    }

}


