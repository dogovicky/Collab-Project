package com.capricon.Collab_Project.service;

import com.capricon.Collab_Project.dto.AuthResponse;
import com.capricon.Collab_Project.dto.UserDTO;
import com.capricon.Collab_Project.dto.ValidationRequest;
import com.capricon.Collab_Project.exception.BaseException;
import com.capricon.Collab_Project.exception.BusinessException;
import com.capricon.Collab_Project.exception.UserException;
import com.capricon.Collab_Project.exception.ValidationException;
import com.capricon.Collab_Project.model.User;
import com.capricon.Collab_Project.model.enums.Gender;
import com.capricon.Collab_Project.repository.UserRepo;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class SignUpService {

    private final UserRepo userRepo;
    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtService jwtService;
    private final Executor executor;

    public SignUpService(UserRepo userRepo, PasswordEncoder passwordEncoder, MailService mailService,
                         JwtService jwtService, Validator validator, Executor executor) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.jwtService = jwtService;
        this.validator = validator;
        this.executor = executor;
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
            Throwable cause = ex.getCause();
            log.error("Error during sign up for user {}: {}", request.getUsername(), ex.getMessage());

            if (cause == null) {
                throw new BaseException("Validation failed: " + ex.getMessage());
            }

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


    public CompletableFuture<AuthResponse> verifyAccountByCode(ValidationRequest request) {
        return CompletableFuture.supplyAsync(() -> verifyAccount(request), executor)
                .exceptionally(ex -> {
                    Throwable cause = ex.getCause();
                    log.error("Failed to validate account {}: {}", request.getUsername(), ex.getMessage());

                    if (cause == null) {
                        throw new BaseException("Validation failed: " + ex.getMessage());
                    }
                    throw (cause instanceof ValidationException || cause instanceof UserException
                            || cause instanceof BusinessException)
                            ? new CompletionException(cause)
                            : new BaseException("Account validation failed");
                });
    }

    @Transactional
    public AuthResponse verifyAccount(ValidationRequest request) {
        User user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserException("User does not exist"));

        if (!user.getVerificationCode().equals(request.getCode())) {
            throw new ValidationException("Invalid verification code");
        }

        user.setIsEnabled(true);
        user.setVerificationCode(null);
        userRepo.save(user);

        return new AuthResponse("Account validation successful",
                jwtService.generateToken(request.getUsername()));
    }

}
